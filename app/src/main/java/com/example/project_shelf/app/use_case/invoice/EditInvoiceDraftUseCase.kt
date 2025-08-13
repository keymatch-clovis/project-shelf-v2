package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import com.example.project_shelf.app.service.InvoiceService
import com.example.project_shelf.app.service.model.CreateInvoiceInput
import com.example.project_shelf.app.service.model.CreateInvoiceProductInput
import com.example.project_shelf.app.service.model.EditInvoiceDraftInput
import com.example.project_shelf.app.use_case.invoice.model.CreateInvoiceProductUseCaseInput
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class EditInvoiceDraftUseCase @Inject constructor(private val service: InvoiceService) {
    data class Input(
        val draftId: Long,
        val date: Date = Date(),
        val products: List<CreateInvoiceProductUseCaseInput> = emptyList(),
        val remainingUnpaidBalance: Long = 0L,
        val customerId: Long? = null,
    )

    suspend fun exec(input: Input) {
        Log.d("USE-CASE", "Editing invoice draft with: $input")
        // FIXME: Maybe we can get this currency unit and locale from a default unique location?
        val currencyUnit = CurrencyUnit.of(Locale.getDefault())

        service.editDraft(
            EditInvoiceDraftInput(
                draftId = input.draftId,
                date = input.date,
                products = input.products.map {
                    CreateInvoiceProductInput(
                        productId = it.productId,
                        price = it.price ?: Money.zero(currencyUnit),
                        count = it.count ?: 0,
                    )
                },
                remainingUnpaidBalance = input.remainingUnpaidBalance,
                customerId = input.customerId,
            )
        )
    }
}