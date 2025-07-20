package com.example.project_shelf.app.entity

import java.math.BigDecimal
import java.util.Date

data class Invoice(
    val id: Long,
    val number: Long,
    val date: Date,
    val discount: BigDecimal?,

    val customer: Customer,
    val products: List<Product>,
)
