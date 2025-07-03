package com.example.project_shelf.adapter.view_model

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

data class CreateProductUiState(
    val rawName: String = "",
    val rawNameErrors: List<ViewModelError> = emptyList(),

    val rawDefaultPrice: String = "",
    val rawPriceErrors: List<ViewModelError> = emptyList(),

    val rawStock: String = "",
    val rawStockErrors: List<ViewModelError> = emptyList(),

    val isValid: Boolean = false,
)

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProductUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.update { it.copy(rawName = value) }
        validateState()
    }

    fun updatePrice(value: String) {
        _uiState.update { it.copy(rawDefaultPrice = value) }
        validateState()
    }

    fun updateCount(value: String) {
        _uiState.update { it.copy(rawStock = value) }
        validateState()
    }

    fun create(onCreated: suspend (product: ProductDto) -> Unit) {
        // NOTE: We should only call this method when all input data has been validated.
        assert(_uiState.value.isValid)

        viewModelScope.launch {
            val product = productRepository.createProduct(
                name = _uiState.value.rawName.trim(),
                price = _uiState.value.rawDefaultPrice.ifBlank { "0" }.toBigDecimal(),
                stock = _uiState.value.rawStock.ifBlank { "0" }.toInt(),
            )

            onCreated(product)
            // NOTE: Clear view model after creation.
            _uiState.update { CreateProductUiState() }
        }
    }

    private fun validateState() {
        val rawNameErrors = validateName()
        val rawPriceErrors = validatePrice()
        val rawStockErrors = validateStock()

        _uiState.update {
            it.copy(
                rawNameErrors = rawNameErrors,
                rawPriceErrors = rawPriceErrors,
                rawStockErrors = rawStockErrors,
                isValid = listOf(
                    rawNameErrors,
                    rawPriceErrors,
                    rawStockErrors,
                ).all {
                    it.isEmpty()
                },
            )
        }
    }

    private fun validateName(): List<ViewModelError> {
        return mutableListOf<ViewModelError>().apply {
            if (_uiState.value.rawName.isBlank()) {
                this.add(ViewModelError.BLANK_VALUE)
            }
        }
    }

    private fun validatePrice(): List<ViewModelError> {
        return mutableListOf<ViewModelError>().apply {
            if (_uiState.value.rawDefaultPrice.isNotBlank()) {
                if (_uiState.value.rawDefaultPrice.toBigDecimalOrNull() == null) {
                    this.add(ViewModelError.INVALID_DECIMAL_VALUE)
                }
            }
        }
    }

    private fun validateStock(): List<ViewModelError> {
        return mutableListOf<ViewModelError>().apply {
            if (_uiState.value.rawStock.isNotBlank()) {
                if (_uiState.value.rawStock.toIntOrNull() == null) {
                    this.add(ViewModelError.INVALID_INTEGER_VALUE)
                }
            }
        }
    }
}