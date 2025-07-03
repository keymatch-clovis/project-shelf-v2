package com.example.project_shelf.app.entity

import java.math.BigInteger
import java.util.Date

data class Invoice(
    val id: Long,
    val customerId: Long,

    val number: Long,
    val date: Date,
    val discount: BigInteger,
)

data class InvoiceProduct(
    val invoiceId: Long,
    val productId: Long,

    val count: Int,
    val price: BigInteger,
    val discount: BigInteger,
)

data class InvoiceFilter(
    val id: Long,
    val number: Long,
    val customerName: String,
    val customerBusinessName: String?,
)