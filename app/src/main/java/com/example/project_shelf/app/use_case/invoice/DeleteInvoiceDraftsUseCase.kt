package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import javax.inject.Inject

class DeleteInvoiceDraftsUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(vararg ids: Long) {
        Log.d("USE-CASE", "Deleting invoice drafts: $ids")
        service.deleteDrafts(*ids)
    }
}