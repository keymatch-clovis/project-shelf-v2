package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.view_model.ProductSearchResultUiState
import com.example.project_shelf.adapter.view_model.ProductUiState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<PagingData<ProductUiState>>
    fun getProducts(name: String): Flow<PagingData<ProductSearchResultUiState>>
    suspend fun createProduct(product: ProductUiState)
    suspend fun removeAll()
}