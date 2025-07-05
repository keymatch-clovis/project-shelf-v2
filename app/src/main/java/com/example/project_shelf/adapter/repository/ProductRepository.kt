package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductRepository {
    fun getProducts(): Flow<PagingData<ProductDto>>
    fun getProducts(name: String): Flow<PagingData<ProductFilterDto>>
    suspend fun getProduct(name: String): ProductDto?
    suspend fun updateProduct(id: Long, name: String, price: BigDecimal, stock: Int): ProductDto
    suspend fun createProduct(name: String, price: BigDecimal, stock: Int): ProductDto
    suspend fun deleteProduct(id: Long)
    suspend fun deleteAll()
}