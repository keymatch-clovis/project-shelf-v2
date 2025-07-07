package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeletionViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _productMarkedForDeletion = MutableStateFlow<ProductDto?>(null)
    val productMarkedForDeletion = _productMarkedForDeletion.asStateFlow()

    fun unmarkProductForDeletion() {
        val product = checkNotNull(productMarkedForDeletion.value) {
            "No product marked for deletion, but tried to unmark."
        }

        Log.d("VIEW-MODEL", "Deleting product: $productMarkedForDeletion")
        viewModelScope.launch {
            productRepository.unmarkForDeletion(product.id)
            _productMarkedForDeletion.update { null }
        }
    }

    fun markProductForDeletion(dto: ProductDto) {
        Log.d("VIEW-MODEL", "Marking product for deletion: $productMarkedForDeletion")
        _productMarkedForDeletion.update { dto }

        viewModelScope.launch {
            productRepository.markForDeletion(dto.id)
        }
    }

    fun clearProductMarkedForDeletion() {
        _productMarkedForDeletion.update { null }
    }
}
