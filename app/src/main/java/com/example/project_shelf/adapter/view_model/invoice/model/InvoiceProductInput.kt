package com.example.project_shelf.adapter.view_model.invoice.model

import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.extension.toFormattedString
import com.example.project_shelf.adapter.view_model.common.extension.toMoney
import com.example.project_shelf.adapter.view_model.common.extension.toMoneyOrZero

data class InvoiceProductInput(
    val productId: Long? = null,
    val name: String? = null,
    val count: Input<String> = Input(),
    val price: Input<String> = Input(),
    val errors: List<ViewModelError> = emptyList(),
) {
    val formattedPrice: String = price.value
        .toMoneyOrZero()
        .toFormattedString()

    val formattedCount: String = count.value
        ?.toIntOrNull()
        // FIXME: Maybe remove that magic string and number? Not sure what to do with it.
        ?.let { if (it > 999) "+999" else it.toString() }
        .let { it ?: "0" }

    fun toDto(): InvoiceProductDto = InvoiceProductDto(
        productId = this.productId!!,
        price = this.price.value!!.toMoney(),
        count = this.count.value!!.toInt(),
    )
}