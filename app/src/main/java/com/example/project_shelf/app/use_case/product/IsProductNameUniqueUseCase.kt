package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class IsProductNameUniqueUseCase @Inject constructor(private val service: ProductService) {
    suspend fun exec(name: String): Boolean {
        Log.d("USE-CASE", "Checking if product name: $name, is unique")
        return service.findByName(name.uppercase()) == null
    }
}