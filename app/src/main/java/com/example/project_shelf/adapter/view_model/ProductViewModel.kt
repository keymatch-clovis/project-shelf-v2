package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class ProductUiState(
    val name: String = "",
    val price: String = "",
    val count: String = "",
)

class ProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductUiState())

    val uiState = _uiState.asStateFlow()
}