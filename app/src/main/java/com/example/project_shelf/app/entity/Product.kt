package com.example.project_shelf.app.entity

import java.math.BigInteger

data class Product(
    val uuid: String,
    val name: String,
    val price: BigInteger = BigInteger.ZERO,
    val count: Int = 0,
)