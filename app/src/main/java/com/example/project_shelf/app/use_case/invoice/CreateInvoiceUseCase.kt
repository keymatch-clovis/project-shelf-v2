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
    data class ProductParam(
        val id: String,
        val price: BigDecimal,
        val discount: BigDecimal,
    )

    suspend fun exec(
        customerId: Long,
        date: Date,
        discount: BigDecimal,
        products: List<ProductParam>,
    ): Invoice {
        Log.d("USE-CASE", "Creating invoice")

        return service.create()
    }
}