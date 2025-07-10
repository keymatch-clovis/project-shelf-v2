package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import javax.inject.Inject

class DeletePendingForDeletionUseCase @Inject constructor(private val service: ProductService) {
    suspend fun exec() {
        Log.d("USE-CASE", "Deleting products pending for deletion")
        return service.deletePendingForDeletion()
    }
}