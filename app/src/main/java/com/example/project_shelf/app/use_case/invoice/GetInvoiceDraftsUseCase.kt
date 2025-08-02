package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.entity.InvoiceDraft
import com.example.project_shelf.app.service.InvoiceService
import javax.inject.Inject

class GetInvoiceDraftsUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(): List<InvoiceDraft> {
        Log.d("USE-CASE", "Getting invoice drafts")
        return service.getDrafts()
    }
}