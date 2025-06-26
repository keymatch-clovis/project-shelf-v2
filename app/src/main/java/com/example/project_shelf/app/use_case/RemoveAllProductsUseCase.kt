package com.example.project_shelf.app.use_case

import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class RemoveAllProductsUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec() {
        productService.removeAll()
    }
}