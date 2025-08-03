package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.InvoiceDraftProduct

data class InvoiceDraftProductDto(
    val productId: Long,
    val count: Int,
    val price: Long,
)

fun InvoiceDraftProduct.toDto() = InvoiceDraftProductDto(
    productId = this.productId,
    count = this.count,
    price = this.price,
)
