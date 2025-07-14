package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.ProductDto
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductRepository {
    fun get(): Flow<PagingData<ProductDto>>

    fun search(value: String): Flow<PagingData<ProductDto>>
    suspend fun isProductNameUnique(name: String): Boolean

    suspend fun update(id: Long, name: String, price: BigDecimal, stock: Int): ProductDto
    suspend fun create(name: String, price: BigDecimal, stock: Int): ProductDto

    suspend fun setPendingForDeletion(id: Long)
    suspend fun unsetPendingForDeletion(id: Long)

    suspend fun deleteAll()
}