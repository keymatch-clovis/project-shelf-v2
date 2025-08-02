package com.example.project_shelf.adapter.dto.data_store

import com.example.project_self.proto.SavedInvoice

data class SavedInvoiceDto(
    val date: Long,
    val customerId: Long,
    val invoiceProducts: List<InvoiceProductDto>,
)

fun SavedInvoice.toDto() = SavedInvoiceDto(
    date = this.date,
    customerId = this.customerId,
    invoiceProducts = this.invoiceProductsList.map { it.toDto() }
)