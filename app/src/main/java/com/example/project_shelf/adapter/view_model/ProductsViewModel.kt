package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    var isLoading: Boolean = false,
    var isShowingCreateProductDialog: Boolean = false,
    var isShowingEditProductDialog: Boolean = false,
    var selectedProduct: ProductUiState? = null,
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    var products: Flow<PagingData<ProductUiState>> = repository.getProducts()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))

    fun openCreateProductDialog() {
        _uiState.update {
            it.copy(isShowingCreateProductDialog = true)
        }
    }

    fun closeCreateProductDialog() {
        _uiState.update {
            it.copy(isShowingCreateProductDialog = false)
        }
    }

    fun openEditProductDialog(product: ProductUiState) {
        _uiState.update {
            it.copy(
                selectedProduct = product,
                isShowingEditProductDialog = true,
            )
        }
    }

    fun closeEditProductDialog() {
        _uiState.update {
            it.copy(
                selectedProduct = null,
                isShowingEditProductDialog = false
            )
        }
    }

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
