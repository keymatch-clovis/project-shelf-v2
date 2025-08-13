package com.example.project_shelf.app.use_case.invoice.model

import org.joda.money.Money

data class CreateInvoiceProductUseCaseInput(
    val productId: Long,
    val price: Money?,
    val count: Int?,
)