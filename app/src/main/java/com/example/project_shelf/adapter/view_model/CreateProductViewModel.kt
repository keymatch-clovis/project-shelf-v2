package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.constraints.notBlank
import io.konform.validation.ifPresent
import io.konform.validation.messagesAtPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import java.io.Serializable
import com.example.project_shelf.R
import com.example.project_shelf.adapter.repository.ProductRepository

data class CreateProductUiState(
    val name: String = "",
    val nameErrors: List<Int> = listOf(),

    val price: String = "",
    val priceErrors: List<Int> = listOf(),

    val count: String = "",
    val countErrors: List<Int> = listOf(),
) : Serializable

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateProductUiState> = MutableStateFlow(
        CreateProductUiState()
    )
    private var _validationState: MutableStateFlow<ValidationResult<CreateProductUiState>?> =
        MutableStateFlow(null)

    val uiState = _uiState.asStateFlow()
    val validationState = _validationState.asStateFlow()

    fun updateName(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(name = value) }
        _validationState.update { validateState(_uiState.value) }
        // Update errors with the validation result
        _uiState.update {
            it.copy(
                nameErrors = validationState.value!!.errors.messagesAtPath(CreateProductUiState::name)
                    .map { it.toInt() })
        }
        // Save the state.
        savedState["uiState"] = _uiState.value
    }

    fun updatePrice(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(price = value) }
        _validationState.update { validateState(_uiState.value) }
        // Update errors with the validation result
        _uiState.update {
            it.copy(
                priceErrors = validationState.value!!.errors.messagesAtPath(CreateProductUiState::price)
                    .map { it.toInt() })
        }
    }

    fun updateCount(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(count = value) }
        _validationState.update { validateState(_uiState.value) }
        // Update errors with the validation result
        _uiState.update {
            it.copy(
                countErrors = validationState.value!!.errors.messagesAtPath(CreateProductUiState::count)
                    .map { it.toInt() })
        }
    }

    fun create(onCreated: suspend (product: ProductUiState) -> Unit) {
        assert(_validationState.value?.isValid == true)

        viewModelScope.launch {
            val product = ProductUiState(
                // NOTE: Don't forget to always trim!
                name = _uiState.value.name.trim(),
                price = _uiState.value.price.trim(),
                count = _uiState.value.count.trim(),
            )
            productRepository.createProduct(product)
            onCreated(product)

            // NOTE: Clear view model after creation.
            _uiState.update { CreateProductUiState() }
        }
    }
}

private val validateState = Validation<CreateProductUiState> {
    CreateProductUiState::name {
        notBlank() hint R.string.err_value_required.toString()
    }

    validate(
        CreateProductUiState::price,
        // If the price is empty, treat it as zero, if it's not, check the value is correct.
        { if (it.price.trim().isNotEmpty()) it.price.toBigDecimalOrNull() else BigDecimal.ZERO },
    ) {
        constrain(R.string.err_decimal_required.toString()) { it != null }
        ifPresent {
            constrain(R.string.err_negative_value.toString()) { it >= BigDecimal.ZERO }
        }
    }

    validate(
        CreateProductUiState::count,
        // If the count is empty, treat it as zero, if it's not, check the value is correct.
        { if (it.count.trim().isNotEmpty()) it.count.toIntOrNull() else 0 },
    ) {
        constrain(R.string.err_integer_required.toString()) { it != null }
        ifPresent {
            constrain(R.string.err_negative_value.toString()) { it >= 0 }
        }
    }
}