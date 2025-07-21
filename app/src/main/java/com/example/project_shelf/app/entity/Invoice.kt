package com.example.project_shelf.app.entity

import java.math.BigDecimal
import java.util.Date

data class Invoice(
    val id: Long,
    val customerId: Long,

    val number: Long,
    val date: Date,
    val discount: BigDecimal?,
)

data class InvoiceWithCustomer(
    val invoice: Invoice,
    val customer: Customer,
)

data class InvoiceWithProducts(
    val invoice: Invoice,
    val products: List<Product>,
)

data class InvoicePopulated(
    val invoice: Invoice,
    val customer: Customer,
    val products: List<Product>,
)