package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import kotlinx.coroutines.flow.Flow

interface ProductService {
    fun getProducts(): Flow<PagingData<Product>>
    suspend fun createProduct(product: Product)
    suspend fun removeAll()
    suspend fun remove(product: Product)
    suspend fun update(product: Product)
}