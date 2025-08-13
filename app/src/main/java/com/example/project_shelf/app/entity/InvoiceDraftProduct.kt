package com.example.project_shelf.app.entity

import org.joda.money.Money

data class InvoiceDraftProduct(
    /// Settings
    val count: Int,
    val price: Money,
    /// Relations
    val productId: Long,
)