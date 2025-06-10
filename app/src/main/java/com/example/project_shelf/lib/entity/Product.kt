package com.example.project_shelf.lib.entity

import java.math.BigInteger

data class Product(
    val name: String,
    val price: BigInteger = BigInteger.ZERO,
    val count: Int = 0,
)