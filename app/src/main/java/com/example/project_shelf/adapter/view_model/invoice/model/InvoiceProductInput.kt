package com.example.project_shelf.adapter.view_model.invoice.model

import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.Input

data class InvoiceProductInput(
    val productId: Long? = null,
    val name: String? = null,
    val count: Input<String> = Input(),
    val price: Input<String> = Input(),
) {
    // NOTE:
    // > This is a risky transaction!
    // > Cephalon Sark
    fun toDto(): InvoiceProductDto = InvoiceProductDto(
        productId = this.productId!!,
        name = this.name!!,
        price = this.price.value!!.toLong(),
        count = this.count.value!!.toInt(),
    )
}