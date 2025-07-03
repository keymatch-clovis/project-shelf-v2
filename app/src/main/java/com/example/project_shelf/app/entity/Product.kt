package com.example.project_shelf.app.entity

import java.math.BigDecimal

data class Product(
    val id: Long,
    val name: String,
    val defaultPrice: BigDecimal,
    val stock: Int,
)