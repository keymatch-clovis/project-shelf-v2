package com.example.project_shelf.app.entity

import java.math.BigInteger

data class Product(
    val id: Long,
    val name: String,
    val price: BigInteger,
    val count: Int,
)