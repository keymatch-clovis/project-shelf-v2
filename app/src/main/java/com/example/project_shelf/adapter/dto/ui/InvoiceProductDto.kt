package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.invoice.model.InvoiceProductInput
import com.example.project_shelf.app.use_case.invoice.model.CreateInvoiceProductUseCaseInput

data class InvoiceProductDto(
    val productId: Long,
    val name: String,
    val price: Long?,
    val count: Int?,
) {
    fun toInput(): InvoiceProductInput = InvoiceProductInput(
        productId = this.productId,
        name = this.name,
        count = Input(value = this.count?.toString()),
        price = Input(value = this.price?.toString()),
    )

    fun toUseCaseInput() = CreateInvoiceProductUseCaseInput(
        productId = this.productId,
        price = this.price,
        count = this.count,
    )
}
