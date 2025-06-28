package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import java.io.Serializable
import kotlinx.coroutines.flow.StateFlow

data class EditProductUiState(
    val name: String, val price: String, val count: String, val isValid: Boolean = false,

    val errors: MutableMap<String, Throwable?> = mutableMapOf<String, Throwable?>(
        "name" to null,
        "price" to null,
        "count" to null,
    )
) : Serializable

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
) : ViewModel() {
    private lateinit var _uiState: MutableStateFlow<EditProductUiState>
    lateinit var uiState: StateFlow<EditProductUiState>

    fun setProduct(product: ProductUiState) {
        // Update the UI regardless of validation result.
        _uiState = MutableStateFlow(
            EditProductUiState(
                name = product.name,
                // Don't fill the input with zeros. It looks ugly in my opinion.
                price = if (product.price == "0") "" else product.price,
                count = if (product.count == "0") "" else product.count,
            )
        )

        uiState = _uiState.asStateFlow()
    }

    fun updateName(value: String) {
        // Update the UI regardless of validation result.
        _uiState.update { it.copy(name = value) }
        // Save the state.
        savedState["uiState"] = _uiState.value
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