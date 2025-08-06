package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.dto.ui.toFilter
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.SearchExtension
import com.example.project_shelf.adapter.view_model.util.validator.ObjectValidator
import com.example.project_shelf.app.service.InvoiceService
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
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class InvoiceProductState(
    val productId: Long,
    val name: String,
    val count: Int,
    val price: Long,
)

sealed interface CreateInvoiceViewModelState {
    data class State(
        val isLoading: Boolean = false,
        val isSavingDraft: Boolean = false,
        val draftId: Long? = null,
    )

    data class InputState(
        val customer: Input<CustomerFilterDto, CustomerFilterDto> = Input(null, ObjectValidator()),
        val invoiceProducts: MutableStateFlow<List<InvoiceProductState>> = MutableStateFlow(
            emptyList()
        ),
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

    /// State related
    private val _state = MutableStateFlow(CreateInvoiceViewModelState.State())
    val state = _state.asStateFlow()

    /// Input related
    val inputState = CreateInvoiceViewModelState.InputState()

    /// Event related
    sealed interface Event {
        object OpenSearchCustomer : Event
        object OpenSearchProduct : Event
    }

    val eventFlow = MutableSharedFlow<Event>()

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

    /// Related to invoice product adding
    private val _showAddInvoiceProductDialog = MutableStateFlow(false)
    val showAddInvoiceProductBottomSheet = _showAddInvoiceProductDialog.asStateFlow()

    init {
        // When we start this view model, the FIRST thing we need to do is to either create or load
        // a draft, if present.
        viewModelScope.launch {
            if (draft == null) createDraft() else loadDraft(draft)

            startSavingDraft()
        }

        viewModelScope.launch {
            eventFlow.collectLatest {
                when (it) {
                    Event.OpenSearchCustomer -> openCustomerSearchBar()
                    Event.OpenSearchProduct -> openProductSearchBar()
                }
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

    fun openAddInvoiceProductDialog(dto: ProductFilterDto) {
        // This is most likely called after searching a product using the search bar, so close that
        // before opening the dialog.
        closeProductSearchBar()

        // _showAddInvoiceProductDialog.update { true }
        inputState.invoiceProducts.update {
            it + InvoiceProductState(
                productId = dto.id,
                name = dto.name,
                count = 0,
                price = 0L,
            )
        }

        // TODO: Search for the product from the filter, so we can get the info to show in the dialog
        //  after the dialog is closed, we can add the product to the list.
    }

    fun closeAddInvoiceProductDialog() {
        _showAddInvoiceProductDialog.update { false }
    }

    fun updateCustomer(dto: CustomerFilterDto) {
        closeCustomerSearchBar()

        inputState.customer.update(dto)
    }

    /// Private methods
    private suspend fun createDraft() {
        Log.d("VIEW-MODEL", "Creating invoice draft")
        _state.update { it.copy(isSavingDraft = true) }
        createInvoiceDraftUseCase
            .exec()
            .apply {
                _state.update { it.copy(isSavingDraft = false, draftId = this) }
            }
    }

    private suspend fun loadDraft(draft: InvoiceDraftDto) {
        Log.d("VIEW-MODEL", "Loading invoice draft: $draft")
        _state.update { it.copy(isLoading = true) }

        // As we allow customer deletion in our system, we need to check if the customer
        // assigned in the draft still exists.
        draft.customerId?.let {
            searchCustomerUseCase
                .exec(it)
                ?.toFilter()
                .let {
                    inputState.customer.update(it)
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
                Log.d("test", "$draftProduct, $product")
                InvoiceProductState(
                    productId = product.id,
                    name = product.name,
                    count = draftProduct.count,
                    price = draftProduct.price,
                )
            }
            .let { items -> inputState.invoiceProducts.update { items } }

        _state.update { it.copy(isLoading = false, draftId = draft.id) }
    }

    private suspend fun startSavingDraft() {
        //  > Suitable for scenarios where we want to process emissions from multiple flows
        //  > concurrently without waiting for any particular flow to complete.
        // https://www.baeldung.com/kotlin/combining-multiple-flows
        //
        // We need to listen whenever the input state changes.
        merge(
            inputState.customer.rawValue,
            inputState.invoiceProducts,
        )
            .onEach { _state.update { it.copy(isSavingDraft = true) } }
            .debounce(800)
            .collectLatest {
                editInvoiceDraftUseCase.exec(
                    draftId = _state.value.draftId!!,
                    date = Date(),
                    products = inputState.invoiceProducts.value.map {
                        InvoiceService.ProductParam(
                            id = it.productId,
                            count = it.count,
                            price = it.price,
                        )
                    },
                    remainingUnpaidBalance = 0,
                    customerId = inputState.customer.rawValue.value?.id,
                )

                _state.update { it.copy(isSavingDraft = false) }
            }
    }
}