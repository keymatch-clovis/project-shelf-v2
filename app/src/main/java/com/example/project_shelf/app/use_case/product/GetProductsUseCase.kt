package com.example.project_shelf.app.use_case.product

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val service: ProductService) {
    fun exec(): Flow<PagingData<Product>> {
        return service.get()
    }
}