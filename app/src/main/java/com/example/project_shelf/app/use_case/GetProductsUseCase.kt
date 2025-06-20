package com.example.project_shelf.app.use_case

import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(private val productService: ProductService) {
    fun exec(): Flow<List<Product>> {
        // NOTE: We are not doing anything with the getting of the products. Just returning them.
        return productService.getProducts()
    }
}
