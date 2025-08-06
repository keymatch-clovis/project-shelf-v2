package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductFilter
import com.example.project_shelf.common.Id
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductService {
    fun get(): Flow<PagingData<Product>>
    suspend fun findByName(name: String): Product?
    suspend fun findById(id: Id): Product

    fun search(value: String): Flow<PagingData<ProductFilter>>
    suspend fun search(id: Id): Product?

    suspend fun create(name: String, price: BigDecimal, stock: Int): Product
    suspend fun update(id: Long, name: String, price: BigDecimal, stock: Int): Product

    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()

    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)
}