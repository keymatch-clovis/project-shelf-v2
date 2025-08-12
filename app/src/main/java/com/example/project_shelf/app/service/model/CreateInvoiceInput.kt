package com.example.project_shelf.app.service.model

import java.math.BigDecimal
import java.util.Date

data class CreateInvoiceInput(
    val number: Long,
    val customerId: Long,
    val date: Date,
    val products: List<CreateInvoiceProductInput>,
    val discount: BigDecimal?,
)