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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

data class InputState(
    val defaultPriceInputValue: String = "",
    val stockInputValue: String = "",
)

data class ValidationState(
    val nameErrors: List<ViewModelError> = emptyList(),
    val defaultPriceErrors: List<ViewModelError> = emptyList(),
    val stockErrors: List<ViewModelError> = emptyList(),
)

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

    private val _inputState = MutableStateFlow(InputState())
    val inputState = _inputState.asStateFlow()

    private val _validationState = MutableStateFlow(ValidationState())
    val validationState = _validationState.asStateFlow()

    // We ned this one outside the UI State, as it takes a more important responsibility than the
    // other input elements.
    private val _nameInputValue = MutableStateFlow("")
    val nameInputValue = _nameInputValue.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()


    init {
        // When the name changes, we need to check if another product has this name.
        viewModelScope.launch {
            _nameInputValue
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
                Log.d("TEST", state.toString())
                _validationState.update {
                    it.copy(
                        defaultPriceErrors = state.defaultPriceInputValue.validateBigDecimal(),
                        stockErrors = state.stockInputValue.validateInt(),
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
                        state.defaultPriceErrors,
                        state.stockErrors,
                    ).all { it.isEmpty() }
                }
            }
        }
    }

    fun updateNameInputValue(value: String) {
        _nameInputValue.update { value }
    }

    fun updateDefaultPriceInputValue(value: String) {
        _inputState.update { it.copy(defaultPriceInputValue = value) }
    }

    fun updateStockInputValue(value: String) {
        _inputState.update { it.copy(stockInputValue = value) }
    }

    fun create() {
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        viewModelScope.launch {
            val product = productRepository.createProduct(
                name = _nameInputValue.value.trim(),
                price = _inputState.value.defaultPriceInputValue.toBigDecimalOrZero(),
                stock = _inputState.value.stockInputValue.toIntOrZero(),
            )
            _eventFlow.emit(Event.ProductCreated(product))
        }
    }
}