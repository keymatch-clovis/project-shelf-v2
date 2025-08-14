package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.adapter.ViewModelError
import org.joda.money.Money

data class InvoiceProductDto(
    val productId: Long,
    val name: String,
    val price: Money,
    val count: Int,
    val errors: List<ViewModelError> = emptyList(),
)
