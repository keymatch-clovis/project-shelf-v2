package com.example.project_shelf.app.entity

data class Product(
    val id: Long,
    val name: String,
    val defaultPrice: Long,
    val stock: Int,
)