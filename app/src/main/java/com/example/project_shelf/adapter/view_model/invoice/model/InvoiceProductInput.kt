package com.example.project_shelf.adapter.view_model.invoice.model

import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.extension.toMoney

data class InvoiceProductInput(
    val productId: Long? = null,
    val name: String? = null,
    val count: Input<String> = Input(),
    val price: Input<String> = Input(),
    val errors: List<ViewModelError> = emptyList(),
) {
    fun toDto(): InvoiceProductDto = InvoiceProductDto(
        productId = this.productId!!,
        price = this.price.value!!.toMoney(),
        count = this.count.value!!.toInt(),
    )
}