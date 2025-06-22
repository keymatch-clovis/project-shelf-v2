package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
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

import com.example.project_shelf.R
import com.example.project_shelf.adapter.handler.ProductHandler
import java.io.Serializable

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
    private val productHandler: ProductHandler,
) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateProductUiState> =
        savedState.getMutableStateFlow<CreateProductUiState>(
            "uiState",
            CreateProductUiState(),
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

    fun create() {
        assert(_validationState.value?.isValid == true)
        viewModelScope.launch {
            productHandler.create(_uiState.value.name, _uiState.value.price, _uiState.value.count)
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