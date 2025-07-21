package com.example.project_shelf.app.entity

import java.math.BigDecimal

data class InvoiceProduct(
    val invoiceId: Long,
    val productId: Long,

    val count: Int,
    val price: BigDecimal,
    val discount: BigDecimal?,
)
