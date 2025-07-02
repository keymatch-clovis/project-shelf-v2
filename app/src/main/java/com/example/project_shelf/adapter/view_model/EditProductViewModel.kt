package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val name: String,
    val price: String,
    val count: String,
    val isValid: Boolean = false,
    val isShowingConfirmDeletionDialog: Boolean = false,

    val errors: MutableMap<String, Throwable?> = mutableMapOf<String, Throwable?>(
        "name" to null,
        "price" to null,
        "count" to null,
    )
) : Serializable

@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val product: ProductUiState,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(product: ProductUiState): EditProductViewModel
    }

    private var _uiState = MutableStateFlow(
        EditProductUiState(
            name = product.name,
            // Don't fill the input with zeros. It looks ugly in my opinion.
            price = if (product.price == "0") "" else product.price,
            count = if (product.count == "0") "" else product.count,
        )
    )
    var uiState = _uiState.asStateFlow()

    fun delete(onDelete: () -> Unit) {
        viewModelScope.launch {
            onDelete()
        }
    }

    fun openConfirmDeletionDialog() {
        _uiState.update { it.copy(isShowingConfirmDeletionDialog = true) }
    }

    fun closeConfirmDeletionDialog() {
        _uiState.update { it.copy(isShowingConfirmDeletionDialog = false) }
    }

    fun updateName(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(name = value) }
    }

    fun updatePrice(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(price = value) }
    }

    fun updateCount(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(count = value) }
    }

    fun create() {
        assert(isValid())
    }

    private fun isValid(): Boolean {
        return _uiState.value.errors.filterValues { it != null }.isEmpty()
    }
}