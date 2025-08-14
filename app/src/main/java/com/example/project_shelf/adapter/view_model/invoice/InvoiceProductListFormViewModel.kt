package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftProductDto
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.app.use_case.product.SearchProductUseCase
import com.example.project_shelf.common.Id
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.joda.money.Money

// NOTE:
//  For naming ViewModels:
//   - Action  / Feature / UI Scope
//   - Feature / Purpose / UI Scope
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = InvoiceProductListFormViewModel.Factory::class)
class InvoiceProductListFormViewModel @AssistedInject constructor(
    @Assisted val draftProducts: List<InvoiceDraftProductDto>,
    private val searchProductUseCase: SearchProductUseCase,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(draftProducts: List<InvoiceDraftProductDto>): InvoiceProductListFormViewModel
    }

    data class State(
        val isLoading: Boolean = false,
        val isShowingAddProductDialog: Boolean = false,
        val invoiceProducts: List<InvoiceProductDto> = emptyList(),
    ) {
        /// Computed properties
        val totalValue: Money = invoiceProducts.fold(
            initial = Money.zero(currencyUnitFromDefaultLocale()),
            operation = { acc, item ->
                acc + item.price
            },
        )
    }

    data class Callback(
        val onOpenSearchProduct: () -> Unit,
        val onAddRequest: (InvoiceProductDto) -> Unit,
        val onEditRequest: (dto: InvoiceProductDto, index: Int) -> Unit,
        val onRemoveRequest: (InvoiceProductDto) -> Unit,
    )

    sealed interface Event {
        data class OnAdded(val dto: InvoiceProductDto) : Event
        data class OnRemoved(val dto: InvoiceProductDto) : Event
    }

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Event related
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (draftProducts.isNotEmpty()) {
            _state.update { it.copy(isLoading = true) }

            viewModelScope.launch(context = Dispatchers.IO) {
                Log.d("VIEW-MODEL", "Loading invoice products from draft: $draftProducts")
                draftProducts.mapNotNull { draftProduct ->
                    // HACK: This is NOT performant. As we are doing as many queries as products are
                    //  saved. This is not problematic for now, but it might be. Idk.
                    searchProductUseCase.exec(draftProduct.productId)?.let { product ->
                        draftProduct to product
                    }
                }.map { (draftProduct, product) ->
                    // As we are loading something from memory, we don't know how much time has
                    // passed, and the state might be incorrect. When creating the DTO, we need to
                    // check the loaded count isn't bigger than the current product stock.
                    val errors: MutableList<ViewModelError> = mutableListOf()
                    if (draftProduct.count > product.stock) {
                        errors.add(ViewModelError.NOT_ENOUGH_PRODUCT_STOCK)
                    }

                    InvoiceProductDto(
                        productId = product.id,
                        name = product.name,
                        price = draftProduct.price,
                        count = draftProduct.count,
                        errors = errors,
                    )
                }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addInvoiceProduct(dto: InvoiceProductDto, index: Int? = null) {
        Log.d("VIEW-MODEL", "Adding invoice product: $dto")

        // This means we are editing a product already present in the list.
        if (index != null) {
            _state.update {
                it.copy(
                    invoiceProducts = it.invoiceProducts.toMutableList().also {
                        it[index] = dto
                    },
                )
            }
        }
        // This means it is a new product.
        else {
            // Remove any occurrences of the product if present in the list.
            // NOTE: This is different from `removeInvoiceProduct`, as we don't
            //  want to emit an event for this one.
            _state.update {
                it.copy(invoiceProducts = it.invoiceProducts.filter {
                    it.productId != dto.productId
                })
            }

            _state.update { it.copy(invoiceProducts = it.invoiceProducts + dto) }
        }


        viewModelScope.launch(context = Dispatchers.Main) {
            _eventFlow.emit(Event.OnAdded(dto))
        }
    }

    fun removeInvoiceProduct(dto: InvoiceProductDto) {
        Log.d("VIEW-MODEL", "Deleting invoice product: $dto")
        _state.update { it.copy(invoiceProducts = it.invoiceProducts - dto) }

        viewModelScope.launch(context = Dispatchers.Main) {
            _eventFlow.emit(Event.OnRemoved(dto))
        }
    }
}