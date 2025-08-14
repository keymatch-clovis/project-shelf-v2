package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class UnsetProductPendingForDeletionUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(id: Long) {
        Log.d("USE-CASE", "Product[$id]: Unsetting product pending for deletion")
        productService.unsetPendingForDeletion(id)
    }
}
