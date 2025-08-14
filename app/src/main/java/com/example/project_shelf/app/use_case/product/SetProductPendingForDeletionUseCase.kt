package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.use_case.DELETION_TIMEOUT
import com.example.project_shelf.common.Id
import java.util.Date
import javax.inject.Inject

class SetProductPendingForDeletionUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(id: Id) {
        Log.d("USE-CASE", "Marking product with ID: $id for deletion")
        val timeout = Date().time + DELETION_TIMEOUT.inWholeMilliseconds

        productService.setPendingForDeletion(id, timeout)
    }
}