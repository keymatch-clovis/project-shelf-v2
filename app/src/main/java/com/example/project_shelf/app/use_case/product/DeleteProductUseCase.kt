package com.example.project_shelf.app.use_case.product

import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        id: Long,
    ) {
        productService.delete(id)
    }
}