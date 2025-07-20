package com.example.project_shelf.app.entity

import java.math.BigInteger

data class InvoiceProduct(
    val invoiceId: Long,
    val productId: Long,

    val count: Int,
    val price: BigInteger,
    val discount: BigInteger,
)
