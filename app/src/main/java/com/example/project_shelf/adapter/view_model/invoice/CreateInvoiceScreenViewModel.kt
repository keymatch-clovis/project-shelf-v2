package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import com.example.project_shelf.app.use_case.customer.SearchCustomersUseCase
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceDraftUseCase
import com.example.project_shelf.app.use_case.invoice.CreateInvoiceUseCase
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase
import com.example.project_shelf.common.Id
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = CreateInvoiceScreenViewModel.Factory::class)
class CreateInvoiceScreenViewModel @AssistedInject constructor(
    @Assisted val draft: InvoiceDraftDto?,
    private val createInvoiceDraftUseCase: CreateInvoiceDraftUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val searchCustomersUseCase: SearchCustomersUseCase,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(draft: InvoiceDraftDto?): CreateInvoiceScreenViewModel
    }

    data class State(
        val draftId: Id? = null,
        val isLoading: Boolean = false,
        val isLoadingDraft: Boolean = false,
        val isSavingDraft: Boolean = false,
        val isInvoiceDetailsFormValid: Boolean = false,
        val isInvoiceProductListFormValid: Boolean = false,
        val isShowingCustomerSearch: Boolean = false,
        val isShowingProductSearch: Boolean = false,
    ) {
        /// Computed properties
        val isValid: Boolean = isInvoiceDetailsFormValid.and(isInvoiceProductListFormValid)
            .and(!isLoading)
            .and(!isSavingDraft)
            .and(!isLoadingDraft)
    }

    data class Callback(
        val onOpenCustomerSearch: () -> Unit,
        val onCloseCustomerSearch: () -> Unit,
        val onOpenProductSearch: () -> Unit,
        val onCloseProductSearch: () -> Unit,
        val onCreateRequest: () -> Unit,
        val onCloseRequest: () -> Unit,
    )

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Search extensions
    val customerSearch = SearchExtension<CustomerFilterDto>(
        scope = viewModelScope,
        onSearch = {
            searchCustomersUseCase.exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    val productSearch = SearchExtension<ProductFilterDto>(
        scope = viewModelScope,
        onSearch = {
            searchProductsUseCase.exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    init {
        if (draft != null) {
            _state.update { it.copy(draftId = draft.id) }
        } else {
            _state.update { it.copy(isSavingDraft = true) }

            viewModelScope.launch(context = Dispatchers.IO) {
                Log.d("VIEW-MODEL", "Creating invoice draft")
                val draftId = createInvoiceDraftUseCase.exec()

                _state.update { it.copy(isSavingDraft = false, draftId = draftId) }
            }
        }
    }

    fun openCustomerSearch() {
        _state.update {
            it.copy(
                // Always close other search views if open.
                isShowingProductSearch = false,
                isShowingCustomerSearch = true,
            )
        }
    }

    fun closeCustomerSearch() {
        customerSearch.updateQuery("")
        _state.update { it.copy(isShowingCustomerSearch = false) }
    }

    fun openProductSearch() {
        _state.update {
            it.copy(
                // Always close other search views if open.
                isShowingCustomerSearch = false,
                isShowingProductSearch = true,
            )
        }
    }

    fun closeProductSearch() {
        productSearch.updateQuery("")
        _state.update { it.copy(isShowingProductSearch = false) }
    }

    fun createInvoice(
        customerId: Id,
        products: List<InvoiceProductDto>,
    ) {
        assert(products.isNotEmpty())

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(context = Dispatchers.IO) {
            createInvoiceUseCase.exec(
                input = CreateInvoiceUseCase.Input(
                    customerId = customerId,
                    products = products.map {
                        CreateInvoiceProductInput(
                            productId = it.productId,
                            count = it.count,
                            price = it.price,
                        )
                    },
                )
            )

            _state.update { it.copy(isLoading = true) }
        }
    }
}