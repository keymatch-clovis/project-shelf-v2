package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.SearchExtension
import com.example.project_shelf.adapter.view_model.util.validator.ObjectValidator
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceDraftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class InvoiceProductState(
    val productId: Long,
    val name: String,
    val count: Int,
    val price: BigDecimal,
    val discount: BigDecimal?,
)

sealed interface CreateInvoiceViewModelState {
    data class State(
        val isSavingDraft: Boolean = false,
    )

    data class InputState(
        val customer: Input<CustomerFilterDto, CustomerFilterDto> = Input(null, ObjectValidator()),
        val invoiceProducts: MutableStateFlow<List<InvoiceProductState>> = MutableStateFlow(
            emptyList()
        ),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel()
class CreateInvoiceViewModel @Inject constructor(
    customerRepository: CustomerRepository,
    productRepository: ProductRepository,
    createInvoiceDraftUseCase: CreateInvoiceDraftUseCase,
) : ViewModel() {

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
        repository = customerRepository,
    )

    /// Related to product search
    private val _showProductSearchBar = MutableStateFlow(false)
    val showProductSearchBar = _showProductSearchBar.asStateFlow()
    val productSearch = SearchExtension<ProductFilterDto>(
        scope = viewModelScope,
        repository = productRepository,
    )

    /// Related to invoice product adding
    private val _showAddInvoiceProductDialog = MutableStateFlow(false)
    val showAddInvoiceProductBottomSheet = _showAddInvoiceProductDialog.asStateFlow()

    init {
        // Whenever this view model is created, we want to create a matching draft object, so we
        // can start working with it.
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Creating invoice draft")
            _state.update { it.copy(isSavingDraft = true) }
            createInvoiceDraftUseCase.exec()
            delay(5000)
            _state.update { it.copy(isSavingDraft = false) }
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
        repeat(10) {
            inputState.invoiceProducts.update {
                it + InvoiceProductState(
                    productId = dto.id,
                    name = dto.name,
                    count = 0,
                    price = BigDecimal.ZERO,
                    discount = BigDecimal.ZERO,
                )
            }
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
}