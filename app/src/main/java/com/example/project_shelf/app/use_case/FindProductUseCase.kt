package com.example.project_shelf.app.use_case

import android.util.Log
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class FindProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(name: String): Product? {
        Log.d("USE-CASE", "Finding product with: ${name.uppercase()}")
        return productService.getProduct(name.uppercase())
    }
}