package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductFilter
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductService {
    fun find(): Flow<PagingData<Product>>

    fun search(value: String): Flow<PagingData<ProductFilter>>

    suspend fun create(name: String, price: BigDecimal, stock: Int): Product

    suspend fun update(id: Long, name: String, price: BigDecimal, stock: Int): Product

    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()

    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)
}