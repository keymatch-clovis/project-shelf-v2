package com.example.project_shelf.app.service.model

data class CreateInvoiceProductInput(
    val productId: Long,
    val count: Int,
    val price: Long,
)