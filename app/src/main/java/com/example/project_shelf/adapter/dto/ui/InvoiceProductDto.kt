package com.example.project_shelf.adapter.dto.ui

import org.joda.money.Money

data class InvoiceProductDto(
    val productId: Long,
    val count: Int,
    val price: Money,
)
