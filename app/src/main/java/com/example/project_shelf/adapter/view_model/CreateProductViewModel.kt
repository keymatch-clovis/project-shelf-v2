package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.project_shelf.adapter.repository.ProductRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class CreateProductViewModelState {
    data class InputState(
        val price: String = "",
        val stock: String = "",
    )

    data class ValidationState(
        val nameErrors: List<ViewModelError> = emptyList(),
        val priceErrors: List<ViewModelError> = emptyList(),
        val stockErrors: List<ViewModelError> = emptyList(),
    )
}

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    sealed class Event {
        data class ProductCreated(val product: ProductDto) : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    private val _inputState = MutableStateFlow(CreateProductViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    private val _validationState = MutableStateFlow(CreateProductViewModelState.ValidationState())
    val validationState = _validationState.asStateFlow()

    // We ned this one outside the UI State, as it takes a more important responsibility than the
    // other input elements.
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()

    init {
        Log.d("VIEW-MODEL", "Create product")
        // When the name changes, we need to check if another product has this name.
        viewModelScope.launch {
            _name
                // We need to debounce this one, as we are looking up the database.
                .collect {
                    val errors = it.validateString(true).toMutableList()
                    // If we have no errors, we can check the product name.
                    if (errors.isEmpty()) {
                        productRepository.getProduct(it)?.let {
                            errors.add(ViewModelError.PRODUCT_NAME_TAKEN)
                        }
                    }
                    _validationState.update { it.copy(nameErrors = errors) }
                }
        }

        // When all of the other UI inputs change, do the default checks.
        viewModelScope.launch {
            _inputState.collect { state ->
                _validationState.update {
                    it.copy(
                        priceErrors = state.price.validateBigDecimal(),
                        stockErrors = state.stock.validateInt(),
                    )
                }
            }
        }

        // When the validation state changes, check if the view model is valid.
        viewModelScope.launch {
            _validationState.collect { state ->
                _isValid.update {
                    listOf(
                        state.nameErrors,
                        state.priceErrors,
                        state.stockErrors,
                    ).all { it.isEmpty() }
                }
            }
        }
    }

    fun updateName(value: String) {
        _name.update { value }
    }

    fun updatePrice(value: String) {
        _inputState.update { it.copy(price = value) }
    }

    fun updateStock(value: String) {
        _inputState.update { it.copy(stock = value) }
    }

    fun create() {
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        viewModelScope.launch {
            val product = productRepository.createProduct(
                name = _name.value.trim(),
                price = _inputState.value.price.toBigDecimalOrZero(),
                stock = _inputState.value.stock.toIntOrZero(),
            )
            _eventFlow.emit(Event.ProductCreated(product))
        }
    }
}