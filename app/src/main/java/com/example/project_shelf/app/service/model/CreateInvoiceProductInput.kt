package com.example.project_shelf.app.service.model

import org.joda.money.Money

data class CreateInvoiceProductInput(
    val productId: Long,
    val count: Int,
    val price: Money,
)