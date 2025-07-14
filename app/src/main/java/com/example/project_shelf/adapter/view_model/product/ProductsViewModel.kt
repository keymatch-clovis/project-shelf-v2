package com.example.project_shelf.adapter.view_model.product

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
import javax.inject.Inject

@HiltViewModel()
class ProductsViewModel @Inject constructor(
    repository: ProductRepository,
) : ViewModel() {
    var products: Flow<PagingData<ProductDto>> = repository.get()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))
}