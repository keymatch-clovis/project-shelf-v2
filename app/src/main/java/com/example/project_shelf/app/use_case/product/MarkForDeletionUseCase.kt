package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

// This timeout should not be related to Android timeouts (e.g. Dialogs, Toasts, Snack bars, etc.),
// but should be enough for it to be usable. If we set a timeout too low, then the deletion time
// is almost useless.
val PRODUCT_DELETION_TIMEOUT = 20.seconds

class MarkForDeletionUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(id: Long) {
        Log.d("USE-CASE", "Marking product for deletion: $id")
        val timeout = Date().time + PRODUCT_DELETION_TIMEOUT.inWholeMilliseconds

        productService.markForDeletion(id, timeout)
    }
}