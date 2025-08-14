package com.example.project_shelf.app.entity

import org.joda.money.Money

data class Product(
    val id: Long,
    val name: String,
    val defaultPrice: Money,
    val stock: Int,
)