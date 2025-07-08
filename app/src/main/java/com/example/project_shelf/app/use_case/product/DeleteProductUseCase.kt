package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        id: Long,
    ) {
        Log.d("USE-CASE", "Deleting product with ID: $id")
        productService.delete(id)
    }
}