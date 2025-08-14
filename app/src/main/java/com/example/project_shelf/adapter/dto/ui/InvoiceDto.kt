package com.example.project_shelf.adapter.dto.ui

import android.os.Parcelable
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class InvoiceDto(
    val id: Long,
    val number: Long,
    val date: Long,
    val remainingUnpaidBalance: Long,
) : Parcelable

fun Invoice.toDto(): InvoiceDto {
    return InvoiceDto(
        id = this.id,
        number = this.number,
        date = this.date.time,
        remainingUnpaidBalance = this.remainingUnpaidBalance,
    )
}

@Serializable
@Parcelize
data class InvoiceWithCustomerDto(
    val invoice: InvoiceDto,
    val customer: CustomerDto,
) : Parcelable

fun InvoiceWithCustomer.toDto(): InvoiceWithCustomerDto {
    return InvoiceWithCustomerDto(
        invoice = this.invoice.toDto(),
        customer = this.customer.toDto(),
    )
}

