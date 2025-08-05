package com.example.project_shelf.app.entity

data class InvoiceProduct(
    /// Settings
    val count: Int,
    val price: Long,
    /// Relations
    val invoiceId: Long,
    val productId: Long,
)
