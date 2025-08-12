package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toFilter
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.common.validator.validateDouble
import com.example.project_shelf.adapter.view_model.common.validator.validateObject
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.invoice.model.InvoiceProductInput
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import com.example.project_shelf.app.use_case.customer.SearchCustomerUseCase
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceDraftUseCase
import com.example.project_shelf.app.use_case.invoice.EditInvoiceDraftUseCase
import com.example.project_shelf.app.use_case.product.SearchProductUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

sealed interface CreateInvoiceViewModelState {
    data class UiState(
        val isLoading: Boolean = false,
        val isSavingDraft: Boolean = false,
        val draftId: Long? = null,
        val isShowingAddInvoiceProductDialog: Boolean = false,
    )

    data class InputState(
        val customer: Input<CustomerFilterDto> = Input(),
        val invoiceProducts: List<InvoiceProductDto> = emptyList(),
        val currentInvoiceProductInput: InvoiceProductInput = InvoiceProductInput(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = CreateInvoiceViewModel.Factory::class)
class CreateInvoiceViewModel @AssistedInject constructor(
    @Assisted val draft: InvoiceDraftDto?,
    customerRepository: CustomerRepository,
    productRepository: ProductRepository,
    private val createInvoiceDraftUseCase: CreateInvoiceDraftUseCase,
    private val searchCustomerUseCase: SearchCustomerUseCase,
    private val editInvoiceDraftUseCase: EditInvoiceDraftUseCase,
    private val searchProductUseCase: SearchProductUseCase,
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
        onSearch = { productRepository.search(it) },
    )

    init {
        // When we start this view model, the FIRST thing we need to do is to either create or load
        // a draft, if present.
        viewModelScope.launch {
            if (draft == null) createDraft() else loadDraft(draft)

            startSavingDraft()
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

    fun openAddInvoiceProductDialog(dto: InvoiceProductDto) {
        closeProductSearchBar()

        _inputState.value.invoiceProducts
            .first { it.productId == dto.productId }
            .let {
                _inputState.update { it.copy(currentInvoiceProductInput = dto.toInput()) }
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
            _inputState.update { it.copy(currentInvoiceProductInput = invoiceProduct.toInput()) }
        } else {
            _inputState.update {
                it.copy(
                    currentInvoiceProductInput = InvoiceProductInput(
                        productId = dto.id,
                        name = dto.name,
                        count = Input(),
                        price = Input(),
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
                price = Input(value = value, errors = value.validateDouble())
            )
        )
    }

    fun clearCurrentInvoiceProduct() = _inputState.update {
        it.copy(currentInvoiceProductInput = InvoiceProductInput())
    }

    fun addCurrentInvoiceProduct() {
        // NOTE:
        // > This is a risky transaction!
        // > Cephalon Sark
        val dto = InvoiceProductDto(
            productId = _inputState.value.currentInvoiceProductInput.productId!!,
            name = _inputState.value.currentInvoiceProductInput.name!!,
            price = _inputState.value.currentInvoiceProductInput.price.value?.toLong() ?: 0L,
            count = _inputState.value.currentInvoiceProductInput.count.value?.toInt() ?: 0,
        )

        // Delete any occurrences of the current invoice product, if any.
        deleteInvoiceProduct(dto)

        _inputState.update { it.copy(invoiceProducts = it.invoiceProducts + dto) }

        closeAddInvoiceProductDialog()
    }

    fun deleteInvoiceProduct(dto: InvoiceProductDto) {
        Log.d("VIEW-MODEL", "Deleting invoice product: $dto")
        _inputState.update {
            it.copy(
                invoiceProducts = it.invoiceProducts.filter { it.productId != dto.productId },
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
                InvoiceProductDto(
                    productId = product.id,
                    name = product.name,
                    price = draftProduct.price,
                    count = draftProduct.count,
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
                        products = it.invoiceProducts.map { it.toUseCaseInput() },
                        customerId = it.customer.value?.id,
                        // TODO: FIX THIS.
                        remainingUnpaidBalance = 0,
                    )
                )

                _uiState.update { it.copy(isSavingDraft = false) }
            }
    }
}