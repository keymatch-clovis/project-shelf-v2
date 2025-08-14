package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.app.service.model.CreateInvoiceDraftInput
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import com.example.project_shelf.common.Id
import java.util.Date
import javax.inject.Inject

class CreateInvoiceDraftUseCase @Inject constructor(private val service: InvoiceService) {
    suspend fun exec(
        date: Date = Date(),
        products: List<CreateInvoiceProductInput> = emptyList(),
        remainingUnpaidBalance: Long = 0,
        customerId: Long? = null,
    ): Id {
        Log.d("USE-CASE", "Creating invoice draft")
        return service.createDraft(
            CreateInvoiceDraftInput(
                date = date,
                products = products,
                remainingUnpaidBalance = remainingUnpaidBalance,
                customerId = customerId,
            )
        )
    }
}