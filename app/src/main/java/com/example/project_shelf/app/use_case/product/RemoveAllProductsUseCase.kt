package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class RemoveAllProductsUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec() {
        Log.d("USE-CASE", "Deleting all products")
        productService.delete()
    }
}