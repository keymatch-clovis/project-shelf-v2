package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.Product
import org.joda.money.Money

data class ProductDto(
    val id: Long,
    val name: String,
    val defaultPrice: Money,
    val stock: Int,
)

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = this.id,
        name = this.name,
        defaultPrice = this.defaultPrice,
        stock = this.stock,
    )
}