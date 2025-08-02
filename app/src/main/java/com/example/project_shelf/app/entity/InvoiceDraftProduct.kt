package com.example.project_shelf.app.entity

data class InvoiceDraftProduct(
    /// Properties
    val count: Int,
    val price: Long,
    /// Relations
    val productId: Long,
)