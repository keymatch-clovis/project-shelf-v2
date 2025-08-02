package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import java.util.Date
import javax.inject.Inject

class CreateInvoiceDraftUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(
        date: Date = Date(),
        products: List<InvoiceService.ProductParam> = emptyList(),
        remainingUnpaidBalance: Long = 0,
        customerId: Long? = null,
    ): Long {
        Log.d("USE-CASE", "Creating invoice draft")
        return service.createDraft(
            date = date,
            products = products,
            remainingUnpaidBalance = remainingUnpaidBalance,
            customerId = customerId,
        )
    }
}