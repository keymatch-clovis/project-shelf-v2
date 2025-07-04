package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProductUiState(
    val rawName: String = "",
    val rawNameErrors: List<ViewModelError> = emptyList(),

    val rawDefaultPrice: String = "",
    val rawPriceErrors: List<ViewModelError> = emptyList(),

    val rawStock: String = "",
    val rawStockErrors: List<ViewModelError> = emptyList(),

    val isValid: Boolean = false,

    val showConfirmDeletionDialog: Boolean = false,
)

@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val product: ProductDto,
    private val productRepository: ProductRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(product: ProductDto): EditProductViewModel
    }

    private var _uiState = MutableStateFlow(
        EditProductUiState(
            rawName = product.name,
            // Don't fill the input with zeros. It looks ugly in my opinion.
            rawDefaultPrice = if (product.realDefaultPrice == "0") "" else product.realDefaultPrice,
            rawStock = if (product.stock == "0") "" else product.realDefaultPrice,
        )
    )
    var uiState = _uiState.asStateFlow()

    fun delete(onDelete: () -> Unit) {
        viewModelScope.launch {
            onDelete()
        }
    }

    fun openConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = true) }
    }

    fun closeConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = false) }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(rawName = value) }
        validateState()
    }

    fun updatePrice(value: String) {
        val errors = mutableListOf<ViewModelError>()
        if (value.isBlank()) {
            errors.add(ViewModelError.BLANK_VALUE)
        }
        if (value.toBigDecimalOrNull() == null) {
            errors.add(ViewModelError.INVALID_DECIMAL_VALUE)
        }

        _uiState.update { it.copy(rawDefaultPrice = value, rawPriceErrors = errors.toList()) }
    }

    fun updateCount(value: String) {
        val errors = mutableListOf<ViewModelError>()
        if (value.isBlank()) {
            errors.add(ViewModelError.BLANK_VALUE)
        }
        if (value.toIntOrNull() == null) {
            errors.add(ViewModelError.INVALID_INTEGER_VALUE)
        }

        _uiState.update { it.copy(rawStock = value, rawStockErrors = errors.toList()) }
    }

    fun update() {
        assert(_uiState.value.isValid)

        viewModelScope.launch {
            val product = productRepository.updateProduct(
                id = product.id,
                name = _uiState.value.rawName.trim(),
                price = _uiState.value.rawDefaultPrice.ifBlank { "0" }.toBigDecimal(),
                stock = _uiState.value.rawStock.ifBlank { "0" }.toInt(),
            )
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