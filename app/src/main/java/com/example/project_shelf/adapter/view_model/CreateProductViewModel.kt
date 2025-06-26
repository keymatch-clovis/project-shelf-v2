package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.Serializable
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.app.validator.validateProductCount
import com.example.project_shelf.app.validator.validateProductName
import com.example.project_shelf.app.validator.validateProductPrice

data class CreateProductUiState(
    val name: String = "",
    val price: String = "",
    val count: String = "",
    val isValid: Boolean = false,

    val errors: MutableMap<String, Throwable?> = mutableMapOf<String, Throwable?>(
        "name" to null,
        "price" to null,
        "count" to null,
    )
) : Serializable

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProductUiState())
    val uiState = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.update {
            // FIXME: This is not the correct way of doing this. Or at least it feels like so.
            it.errors["name"] = validateProductName(value).exceptionOrNull()
            it.copy(errors = it.errors)
        }

        // Update the UI regardless of validation result.
        _uiState.update { it.copy(name = value, isValid = isValid()) }
    }

    fun updatePrice(value: String) {
        validateProductPrice(value).onFailure { throwable ->
            _uiState.update {
                // FIXME: This is not the correct way of doing this. Or at least it feels like so.
                it.errors["price"] = throwable
                it.copy(errors = it.errors)
            }
        }

        // Update the UI regardless of validation result.
        _uiState.update { it.copy(price = value, isValid = isValid()) }
    }

    fun updateCount(value: String) {
        validateProductCount(value).onFailure { throwable ->
            _uiState.update {
                // FIXME: This is not the correct way of doing this. Or at least it feels like so.
                it.errors["count"] = throwable
                it.copy(errors = it.errors)
            }
        }

        // Update the UI regardless of validation result.
        _uiState.update { it.copy(count = value, isValid = isValid()) }
    }

    fun create(onCreated: suspend (product: ProductUiState) -> Unit) {
        assert(isValid())

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

    private fun isValid(): Boolean {
        return _uiState.value.errors.filterValues { it != null }.isEmpty()
    }
}