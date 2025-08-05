package com.example.project_shelf.app.entity

import java.util.Date

data class InvoiceDraft(
    /// Properties
    val id: Long,
    val date: Date,
    val remainingUnpaidBalance: Long,
    val products: List<InvoiceDraftProduct>,
    /// Relations
    val customerId: Long?,
)