package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

data class EditProductUiState(
    val rawName: String = "",
    val rawNameErrors: List<ViewModelError> = emptyList(),

    val rawDefaultPrice: String = "",
    val rawPriceErrors: List<ViewModelError> = emptyList(),

    val rawStock: String = "",
    val rawStockErrors: List<ViewModelError> = emptyList(),

    val isValid: Boolean = false,

    val showConfirmDeletionDialog: Boolean = false,
) : Serializable

@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val product: ProductDto,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(product: ProductDto): EditProductViewModel
    }

    private var _uiState = MutableStateFlow(
        EditProductUiState(
            rawName = product.name,
            // Don't fill the input with zeros. It looks ugly in my opinion.
            rawDefaultPrice = product.realDefaultPrice,
            rawStock = product.stock,
        )
    )
    var uiState = _uiState.asStateFlow()

    val isValid: Boolean
        get() = listOf(
            _uiState.value.rawNameErrors,
            _uiState.value.rawPriceErrors,
            _uiState.value.rawStockErrors,
        ).all { it.isEmpty() }

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
        val errors = mutableListOf<ViewModelError>()
        if (value.isBlank()) {
            errors.add(ViewModelError.BLANK_VALUE)
        }

        _uiState.update { it.copy(rawName = value, rawNameErrors = errors.toList()) }
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

    fun create() {
        assert(isValid)
    }
}