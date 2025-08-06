package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import java.util.Date
import javax.inject.Inject

class EditInvoiceDraftUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(
        draftId: Long,
        date: Date = Date(),
        products: List<InvoiceService.ProductParam> = emptyList(),
        remainingUnpaidBalance: Long = 0,
        customerId: Long? = null,
    ) {
        Log.d(
            "USE-CASE",
            "Editing invoice draft with: $draftId, $date, $products, $remainingUnpaidBalance, $customerId"
        )
        service.editDraft(
            draftId = draftId,
            date = date,
            products = products,
            remainingUnpaidBalance = remainingUnpaidBalance,
            customerId = customerId,
        )
    }
}