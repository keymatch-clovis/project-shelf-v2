package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductSearch
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface ProductService {
    fun getProducts(): Flow<PagingData<Product>>
    fun getProducts(name: String): Flow<PagingData<ProductSearch>>
    suspend fun createProduct(name: String, price: BigInteger = BigInteger.ZERO, count: Int = 0)
    suspend fun removeAll()
    suspend fun remove(product: Product)
    suspend fun update(product: Product)
}