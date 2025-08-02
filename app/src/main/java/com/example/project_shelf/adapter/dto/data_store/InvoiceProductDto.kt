package com.example.project_shelf.adapter.dto.data_store

import com.example.project_self.proto.InvoiceProduct

data class InvoiceProductDto(
    val productId: Long,
    val count: Int,
    val price: Long,
    val discount: Long,
)

fun InvoiceProduct.toDto(): InvoiceProductDto = InvoiceProductDto(
    productId = this.productId,
    count = this.count,
    price = this.price,
    discount = this.discount,
)
