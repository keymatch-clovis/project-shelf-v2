package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.extension.toFormattedString
import com.example.project_shelf.adapter.view_model.invoice.model.InvoiceProductInput
import org.joda.money.Money
import java.math.BigDecimal

data class InvoiceProductDto(
    val productId: Long,
    val count: Int,
    val price: Money,
) {
    /// Computed properties
    val formattedPrice: String = price.toFormattedString()

    val formattedCount: String = count
        // FIXME: Maybe remove that magic string and number? Not sure what to do with it.
        .let { if (it > 999) "+999" else it.toString() }

    fun toInput(): InvoiceProductInput {
        // Remove trailing zeros that can interfere with the correct representation of the price.
        val price = this.price.amount.stripTrailingZeros()

        return InvoiceProductInput(
            productId = this.productId,
            // NOTE: I feel this is correct UX---If the value is zero, put an empty string instead.
            count = Input(value = if (count == 0) null else count.toString()),
            price = Input(value = if (price == BigDecimal.ZERO) null else price.toString()),
        )
    }
}
