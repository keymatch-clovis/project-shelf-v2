package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.service.InvoiceService
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class CreateInvoiceUseCase @Inject constructor(
    private val service: InvoiceService,
) {
    suspend fun exec(
        customerId: Long,
        products: List<InvoiceService.ProductParam>,
        date: Date = Date(),
        discount: BigDecimal? = null,
    ): Invoice {
        Log.d("USE-CASE", "Creating invoice with: $customerId, $products, $date, $discount")
        assert(products.isNotEmpty())

        // First, get the consecutive number we are going to assign to this invoice.
        // NOTE:
        //  We are doing a simple numbering, we just take the latest number + 1.
        Log.d("USE-CASE", "Getting consecutive number")
        val consecutiveNumber = service.getCurrentNumber() + 1

        val invoiceId = service.create(
            number = consecutiveNumber,
            customerId = customerId,
            products = products,
            date = date,
            discount = discount,
        )

        return Invoice(
            id = invoiceId,
            number = consecutiveNumber,
            customerId = customerId,
            date = date,
            // TODO: THIS
            remainingUnpaidBalance = 0,
        )
    }
}