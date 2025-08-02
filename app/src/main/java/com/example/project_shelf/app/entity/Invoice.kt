package com.example.project_shelf.app.entity

import java.util.Date

data class Invoice(
    val id: Long,
    /// Properties
    val number: Long,
    val date: Date,
    val remainingUnpaidBalance: Long,
    /// Relations
    val customerId: Long,
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