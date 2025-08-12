package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.app.service.model.CreateInvoiceInput
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

class CreateInvoiceUseCase @Inject constructor(
    private val service: InvoiceService,
) {
    // https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb
    suspend fun exec(
        customerId: Long,
        products: List<CreateInvoiceProductInput>,
        date: Date = Date(),
        discount: BigDecimal? = null,
    ): Invoice = withContext(Dispatchers.IO) {
        Log.d("USE-CASE", "Creating invoice with: $customerId, $products, $date, $discount")
        assert(products.isNotEmpty())

        // First, get the consecutive number we are going to assign to this invoice.
        // NOTE:
        //  We are doing a simple numbering, we just take the latest number + 1.
        Log.d("USE-CASE", "Getting consecutive number")
        val consecutiveNumber = service.getCurrentNumber() + 1

        val invoiceId = service.create(
            CreateInvoiceInput(
                number = consecutiveNumber,
                customerId = customerId,
                products = products,
                date = date,
                discount = discount,
            )
        )

        Invoice(
            id = invoiceId,
            number = consecutiveNumber,
            customerId = customerId,
            date = date,
            // TODO: THIS
            remainingUnpaidBalance = 0,
        )
    }
}