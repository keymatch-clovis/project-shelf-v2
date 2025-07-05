package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductFilter
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductService {
    fun getProducts(): Flow<PagingData<Product>>
    fun getProducts(name: String): Flow<PagingData<ProductFilter>>
    suspend fun getProduct(name: String): Product?

    suspend fun create(name: String, price: BigDecimal, stock: Int): Product
    suspend fun update(id: Long, name: String, price: BigDecimal, stock: Int): Product
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}