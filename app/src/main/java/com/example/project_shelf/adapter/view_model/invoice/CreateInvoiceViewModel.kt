package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.dto.ui.toFilter
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.adapter.view_model.common.extension.toMoney
import com.example.project_shelf.adapter.view_model.common.extension.toMoneyOrZero
import com.example.project_shelf.adapter.view_model.common.validator.validateBigDecimal
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.adapter.view_model.common.validator.validateObject
import com.example.project_shelf.adapter.view_model.invoice.model.InvoiceProductInput
import com.example.project_shelf.app.use_case.customer.SearchCustomerUseCase
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceDraftUseCase
import com.example.project_shelf.app.use_case.invoice.EditInvoiceDraftUseCase
import com.example.project_shelf.app.use_case.invoice.model.CreateInvoiceProductUseCaseInput
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import com.example.project_shelf.app.use_case.product.SearchProductUseCase
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase_Factory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.money.Money
import java.util.Date

sealed interface CreateInvoiceViewModelState {
    data class UiState(
        val isLoading: Boolean = false,
        val isSavingDraft: Boolean = false,
        val isShowingAddInvoiceProductDialog: Boolean = false,
        val draftId: Long? = null,
        val totalValue: Money = Money.zero(currencyUnitFromDefaultLocale()),
    )

    data class InputState(
        val customer: Input<CustomerFilterDto> = Input(),
        val invoiceProducts: List<InvoiceProductInput> = emptyList(),
        val currentInvoiceProductInput: InvoiceProductInput = InvoiceProductInput(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = CreateInvoiceViewModel.Factory::class)
class CreateInvoiceViewModel @AssistedInject constructor(
    @Assisted val draft: InvoiceDraftDto?,
    customerRepository: CustomerRepository,
    private val createInvoiceDraftUseCase: CreateInvoiceDraftUseCase,
    private val searchCustomerUseCase: SearchCustomerUseCase,
    private val editInvoiceDraftUseCase: EditInvoiceDraftUseCase,
    private val searchProductUseCase: SearchProductUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val findProductUseCase: FindProductUseCase,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(draft: InvoiceDraftDto?): CreateInvoiceViewModel
    }

    /// UiState related
    private val _uiState = MutableStateFlow(CreateInvoiceViewModelState.UiState())
    val uiState = _uiState.asStateFlow()

    /// Input related
    private val _inputState = MutableStateFlow(CreateInvoiceViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    /// Related to customer search.
    private val _showCustomerSearchBar = MutableStateFlow(false)
    val showCustomerSearchBar = _showCustomerSearchBar.asStateFlow()
    val customerSearch = SearchExtension<CustomerFilterDto>(
        scope = viewModelScope,
        onSearch = { customerRepository.search(it) },
    )

    /// Related to product search
    private val _showProductSearchBar = MutableStateFlow(false)
    val showProductSearchBar = _showProductSearchBar.asStateFlow()
    val productSearch = SearchExtension<ProductFilterDto>(
        scope = viewModelScope,
        onSearch = {
            searchProductsUseCase
                .exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    init {
        // When we start this view model, the FIRST thing we need to do is to either create or load
        // a draft, if present.
        viewModelScope.launch {
            if (draft == null) createDraft() else loadDraft(draft)

            startSavingDraft()
        }

        // Start calculating the total value of the invoice when the invoice products change.
        viewModelScope.launch {
            _inputState
                .distinctUntilChangedBy { _inputState.value.invoiceProducts }
                .mapLatest {
                    it.invoiceProducts.fold(
                        initial = Money.zero(currencyUnitFromDefaultLocale()),
                        operation = { acc, item ->
                            acc + item.price.value.toMoneyOrZero()
                        },
                    )
                }
                .collectLatest { totalValue ->
                    _uiState.update { it.copy(totalValue = totalValue) }
                }
        }
    }

    fun openCustomerSearchBar() = _showCustomerSearchBar.update { true }
    fun closeCustomerSearchBar() {
        customerSearch.updateQuery("")
        _showCustomerSearchBar.update { false }
    }

    fun openProductSearchBar() = _showProductSearchBar.update { true }
    fun closeProductSearchBar() {
        productSearch.updateQuery("")
        _showProductSearchBar.update { false }
    }

    fun openAddInvoiceProductDialog(input: InvoiceProductInput) {
        closeProductSearchBar()

        _inputState.value.invoiceProducts
            .first { it.productId == input.productId }
            .let {
                _inputState.update { it.copy(currentInvoiceProductInput = input) }
            }

        _uiState.update { it.copy(isShowingAddInvoiceProductDialog = true) }
    }

    fun openAddInvoiceProductDialog(dto: ProductFilterDto) {
        // This is most likely called after searching a product using the search bar, so close that
        // before opening the dialog.
        closeProductSearchBar()

        // We don't allow for multiple instances of the same item in the invoice. If we find the
        // product is already in the list, we must update it instead.
        val invoiceProduct = _inputState.value.invoiceProducts.find { it.productId == dto.id }

        if (invoiceProduct != null) {
            _inputState.update { it.copy(currentInvoiceProductInput = invoiceProduct) }
        } else {
            _inputState.update {
                it.copy(
                    currentInvoiceProductInput = InvoiceProductInput(
                        productId = dto.id,
                        name = dto.name,
                    )
                )
            }
        }

        _uiState.update { it.copy(isShowingAddInvoiceProductDialog = true) }
    }

    fun closeAddInvoiceProductDialog() {
        _uiState.update { it.copy(isShowingAddInvoiceProductDialog = false) }
        clearCurrentInvoiceProduct()
    }

    /// Update methods
    fun updateCustomer(dto: CustomerFilterDto) {
        closeCustomerSearchBar()

        _inputState.update {
            it.copy(
                customer = Input(
                    value = dto,
                    errors = dto.validateObject(required = true),
                )
            )
        }
    }

    fun updateCurrentInvoiceProductPrice(value: String? = null) = _inputState.update {
        it.copy(
            currentInvoiceProductInput = it.currentInvoiceProductInput.copy(
                // We need to set a blank value to null, so the `String` to `BigDecimal`
                // transforming doesn't need another step.
                price = Input(value = value?.ifBlank { null }, errors = value.validateBigDecimal())
            )
        )
    }

    fun updateCurrentInvoiceProductCount(value: String? = null) {
        _inputState.update {
            it.copy(
                currentInvoiceProductInput = it.currentInvoiceProductInput.copy(
                    count = Input(value = value?.ifBlank { null }, errors = value.validateInt())
                )
            )
        }

        // When updating the count, we need to check the updated count isn't bigger than the current
        // product stock. If it is, show an error.
        value
            ?.toIntOrNull()
            ?.let {
                _uiState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    val product =
                        findProductUseCase.exec(_inputState.value.currentInvoiceProductInput.productId!!)

                    if (it > product.stock) {
                        _inputState.update {
                            it.copy(
                                currentInvoiceProductInput = it.currentInvoiceProductInput.copy(
                                    count = it.currentInvoiceProductInput.count.copy(
                                        errors = it.currentInvoiceProductInput.count.errors + ViewModelError.NOT_ENOUGH_PRODUCT_STOCK
                                    )
                                )
                            )
                        }
                    }
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
    }

    fun clearCurrentInvoiceProduct() = _inputState.update {
        it.copy(currentInvoiceProductInput = InvoiceProductInput())
    }

    fun addCurrentInvoiceProduct() {
        // NOTE: This is different from a DTO to Entity conversion. This is a DTO to DTO conversion,
        //  so the way we update these values are different.
        //  ps. And that's why also Cephalon Sark is not talking here. :3
        val input = InvoiceProductInput(
            productId = _inputState.value.currentInvoiceProductInput.productId!!,
            name = _inputState.value.currentInvoiceProductInput.name!!,
            price = _inputState.value.currentInvoiceProductInput.price,
            count = _inputState.value.currentInvoiceProductInput.count,
            errors = _inputState.value.currentInvoiceProductInput.price.errors + _inputState.value.currentInvoiceProductInput.count.errors,
        )

        // Delete any occurrences of the current invoice product, if any.
        deleteInvoiceProduct(input)

        _inputState.update { it.copy(invoiceProducts = it.invoiceProducts + input) }

        closeAddInvoiceProductDialog()
    }

    fun deleteInvoiceProduct(input: InvoiceProductInput) {
        Log.d("VIEW-MODEL", "Deleting invoice product: $input")
        _inputState.update {
            it.copy(
                invoiceProducts = it.invoiceProducts.filter { it.productId != input.productId },
            )
        }
    }

    /// Private methods
    private suspend fun createDraft() {
        Log.d("VIEW-MODEL", "Creating invoice draft")
        _uiState.update { it.copy(isSavingDraft = true) }
        createInvoiceDraftUseCase
            .exec()
            .apply {
                _uiState.update { it.copy(isSavingDraft = false, draftId = this) }
            }
    }

    private suspend fun loadDraft(draft: InvoiceDraftDto) {
        Log.d("VIEW-MODEL", "Loading invoice draft: $draft")
        _uiState.update { it.copy(isLoading = true) }

        // As we allow customer deletion in our system, we need to check if the customer
        // assigned in the draft still exists.
        draft.customerId?.let {
            searchCustomerUseCase
                .exec(it)
                ?.toFilter()
                .let { customer ->
                    _inputState.update { it.copy(customer = Input(value = customer)) }
                }
        }

        // As we allow product deletion in our system, we need to check each product to see if the
        // assigned product in the draft still exists.
        draft.products
            .mapNotNull { draftProduct ->
                // FIXME: This is NOT performant. As we are doing as many queries as products are saved.
                //  This is not problematic for now, but it might be. Idk.
                searchProductUseCase
                    .exec(draftProduct.productId)
                    ?.let {
                        draftProduct to it
                    }
            }
            .map { (draftProduct, product) ->
                InvoiceProductInput(
                    productId = product.id,
                    name = product.name,
                    price = Input(
                        value = draftProduct.price.toString(),
                        errors = draftProduct.price
                            .toString()
                            .validateBigDecimal(),
                    ),
                    count = Input(
                        value = draftProduct.count.toString(),
                        errors = draftProduct.count
                            .toString()
                            .validateInt()
                    ),
                )
            }
            .let { items -> _inputState.update { it.copy(invoiceProducts = items) } }

        _uiState.update { it.copy(isLoading = false, draftId = draft.id) }
    }

    private suspend fun startSavingDraft() {
        _inputState
            .onEach { _uiState.update { it.copy(isSavingDraft = true) } }
            .debounce(800)
            .collectLatest {
                editInvoiceDraftUseCase.exec(
                    EditInvoiceDraftUseCase.Input(
                        draftId = _uiState.value.draftId!!,
                        date = Date(),
                        products = it.invoiceProducts.map {
                            CreateInvoiceProductUseCaseInput(
                                productId = it.productId!!,
                                price = it.price.value?.toMoney(),
                                count = it.count.value?.toInt(),
                            )
                        },
                        customerId = it.customer.value?.id,
                        // TODO: FIX THIS.
                        remainingUnpaidBalance = 0,
                    )
                )

                _uiState.update { it.copy(isSavingDraft = false) }
            }
    }
}