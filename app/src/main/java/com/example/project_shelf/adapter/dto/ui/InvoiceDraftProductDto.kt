package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.InvoiceDraftProduct
import org.joda.money.Money

data class InvoiceDraftProductDto(
    val productId: Long,
    val count: Int,
    val price: Money,
)

fun InvoiceDraftProduct.toDto() = InvoiceDraftProductDto(
    productId = this.productId,
    count = this.count,
    price = this.price,
)
