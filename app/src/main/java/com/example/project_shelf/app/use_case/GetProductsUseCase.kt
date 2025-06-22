package com.example.project_shelf.app.use_case

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val productService: ProductService) {
    fun exec(): Flow<PagingData<Product>> {
        // NOTE: We are not doing anything with the getting of the products. Just returning them.
        return productService.getProducts()
    }
}
