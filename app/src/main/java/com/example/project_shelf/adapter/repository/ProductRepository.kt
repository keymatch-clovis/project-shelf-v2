package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductRepository : WithSearch<ProductFilterDto> {
    fun find(): Flow<PagingData<ProductDto>>
    suspend fun getProduct(name: String): ProductDto?
    suspend fun updateProduct(id: Long, name: String, price: BigDecimal, stock: Int): ProductDto
    suspend fun createProduct(name: String, price: BigDecimal, stock: Int): ProductDto
    suspend fun setPendingForDeletion(id: Long)
    suspend fun unsetPendingForDeletion(id: Long)
    suspend fun deleteAll()
}