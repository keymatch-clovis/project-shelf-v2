package com.example.project_shelf.adapter.view_model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProductsUiState(
    var isLoading: Boolean = false,

    var productsFound: List<ProductDto> = listOf(),
)

@HiltViewModel()
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    var products: Flow<PagingData<ProductDto>> = repository.getProducts()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))
}