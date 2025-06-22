package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.view_model.ProductUiState
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<PagingData<ProductUiState>>
    suspend fun createProduct(product: ProductUiState): Unit
}