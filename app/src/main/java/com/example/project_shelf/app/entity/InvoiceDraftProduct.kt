package com.example.project_shelf.app.entity

data class InvoiceDraftProduct(
    /// Settings
    val count: Int,
    val price: Long,
    /// Relations
    val productId: Long,
)