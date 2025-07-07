package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class DeleteMarkedForDeletionUseCase @Inject constructor(private val service: ProductService) {
    suspend fun exec() {
        Log.d("USE-CASE", "Deleting products marked for deletion.")
        return service.deleteMarkedForDeletion()
    }
}