package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.app.service.model.CreateInvoiceInput
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class CreateInvoiceUseCase @Inject constructor(
    private val service: InvoiceService,
) {
    data class Input(
        val customerId: Long,
        val products: List<CreateInvoiceProductInput>,
    )

    // https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb
    suspend fun exec(input: Input): Invoice = withContext(Dispatchers.IO) {
        Log.d("USE-CASE", "Creating invoice with: $input")
        assert(input.products.isNotEmpty())

        //  We are doing a simple numbering, we just take the latest number + 1.
        Log.d("USE-CASE", "Getting consecutive number")
        val consecutiveNumber = service.getCurrentNumber() + 1

        val date = Date()
        val invoiceId = service.create(
            CreateInvoiceInput(
                number = consecutiveNumber,
                customerId = input.customerId,
                products = input.products,
                date = date,
                discount = TODO(),
            )
        )

        Invoice(
            id = invoiceId,
            number = consecutiveNumber,
            customerId = input.customerId,
            date = date,
            // TODO: THIS
            remainingUnpaidBalance = 0,
        )
    }

    companion object
}