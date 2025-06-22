package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    var isLoading: Boolean = true,
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()
    var products: Flow<PagingData<ProductUiState>> = repository.getProducts()

    fun test() {
        viewModelScope.launch {
            for (i in 0..100) {
                Log.d("PRODUCTS-VIEW-MODEL", "Loading: $i")
                repository.createProduct(
                    ProductUiState(
                        name = "test", price = "1", count = "0"
                    )
                )
            }
        }
    }
}
