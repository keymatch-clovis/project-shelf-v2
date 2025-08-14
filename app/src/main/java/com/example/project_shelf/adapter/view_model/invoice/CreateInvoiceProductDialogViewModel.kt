package com.example.project_shelf.adapter.view_model.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.extension.toMoneyOrZero
import com.example.project_shelf.adapter.view_model.common.validator.validateBigDecimal
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// NOTE: For naming ViewModels: Action / Feature / UI Scope
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel()
class CreateInvoiceProductDialogViewModel @Inject constructor(
    private val findProductUseCase: FindProductUseCase,
) : ViewModel() {
    data class State(
        val isLoading: Boolean = false,

        // This index is used to know if we are editing an invoice product, or
        // creating a new one.
        // NOTE: Idk if this is a correct abstraction, but it is mine. :3
        val index: Int? = null,

        val productId: Long = 0,
        val name: String = "",
        val price: Input = Input(),
        val count: Input = Input(),
    ) {
        /// Computed properties
        val isValid = price.errors.isEmpty()
            .and(count.errors.isEmpty())
    }

    data class Callback(
        val onPriceChange: (String) -> Unit,
        val onCountChange: (String) -> Unit,
        val onCreateRequest: (State) -> Unit,
        val onDismissRequest: () -> Unit,
    )

    /// Properties
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Update related
    fun setInvoiceProduct(dto: InvoiceProductDto, index: Int? = null) {
        _state.update {
            it.copy(
                index = index,
                productId = dto.productId,
                name = dto.name,
            )
        }

        updatePrice(dto.price.amount.toString())
        // NOTE: Maybe it is a bit unnecessary to update the count with known
        //  DTOs, but just to be safe.
        updateCount(dto.count.toString())
    }

    fun updatePrice(value: String) {
        _state.update {
            it.copy(price = Input(value = value, errors = value.validateBigDecimal()))
        }
    }

    fun updateCount(value: String) {
        _state.update {
            it.copy(count = Input(value = value, errors = value.validateInt()))
        }

        // When updating the count, we need to check the updated count isn't bigger than the current
        // product stock.
        val count = value.toIntOrNull()
        if (count != null) {
            _state.update { it.copy(isLoading = true) }
            viewModelScope.launch(context = Dispatchers.IO) {
                assert(_state.value.productId != 0L)

                val product = findProductUseCase.exec(_state.value.productId)
                if (count > product.stock) {
                    // HACK: This looks very ugly. We are copying the count, and copying the errors,
                    //  to add the new one.
                    _state.update {
                        it.copy(
                            count = it.count.copy(
                                errors = it.count.errors + ViewModelError.NOT_ENOUGH_PRODUCT_STOCK
                            )
                        )
                    }
                }

                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clear() {
        _state.update { State() }
    }

    fun toDto(): InvoiceProductDto {
        assert(_state.value.productId != 0L)
        assert(_state.value.name.isNotEmpty())
        assert(_state.value.isValid)

        return InvoiceProductDto(
            productId = _state.value.productId,
            name = _state.value.name,
            price = _state.value.price.value.toMoneyOrZero(),
            count = _state.value.count.value?.toInt() ?: 0,
            errors = _state.value.price.errors + _state.value.count.errors
        )
    }
}