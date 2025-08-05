package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import java.util.Date
import javax.inject.Inject

class SaveInvoiceDraftUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(
        draftId: Long,
        date: Date = Date(),
        products: List<InvoiceService.ProductParam> = emptyList(),
        remainingUnpaidBalance: Long = 0,
        customerId: Long? = null,
    ) {
        Log.d("USE-CASE", "Saving invoice draft")
        service.editDraft(
            draftId = draftId,
            date = date,
            products = products,
            remainingUnpaidBalance = remainingUnpaidBalance,
            customerId = customerId,
        )
    }
}