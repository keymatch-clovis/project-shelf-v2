package com.example.project_shelf.app.use_case

import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(product: Product): Unit {
        return productService.createProduct(product)
    }
}