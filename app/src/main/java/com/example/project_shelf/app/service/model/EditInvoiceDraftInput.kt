package com.example.project_shelf.app.service.model

import java.util.Date

data class EditInvoiceDraftInput(
    val draftId: Long,
    val date: Date,
    val products: List<CreateInvoiceProductInput>,
    val remainingUnpaidBalance: Long,
    val customerId: Long?,
)