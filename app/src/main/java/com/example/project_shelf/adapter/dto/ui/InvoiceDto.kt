package com.example.project_shelf.adapter.dto.ui

import android.icu.util.Currency
import android.os.Parcelable
import com.example.project_shelf.app.entity.Invoice
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class InvoiceDto(
    val id: Long,
    val customer: CustomerDto,
    val products: List<ProductDto>
) : Parcelable

fun Invoice.toDto(currency: Currency): InvoiceDto {
    return InvoiceDto(
        id = this.id,
        customer = this.customer.toDto(),
        products = this.products.map { it.toDto(currency) }
    )
}