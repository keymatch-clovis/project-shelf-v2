package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProductsUiState(
    var products: List<ProductUiState>
)

class ProductsViewModel constructor(products: List<ProductUiState> = listOf()) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState(products))

    val uiState = _uiState.asStateFlow()
}
