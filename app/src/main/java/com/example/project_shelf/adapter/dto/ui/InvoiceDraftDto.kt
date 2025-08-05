package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.InvoiceDraft
import java.util.Date

data class InvoiceDraftDto(
    val id: Long,
    val date: Date,
    val remainingUnpaidBalance: Long,
    val products: List<InvoiceDraftProductDto>,
    val customerId: Long?,
)

fun InvoiceDraft.toDto() = InvoiceDraftDto(
    id = this.id,
    date = this.date,
    remainingUnpaidBalance = this.remainingUnpaidBalance,
    products = this.products.map { it.toDto() },
    customerId = this.customerId,
)