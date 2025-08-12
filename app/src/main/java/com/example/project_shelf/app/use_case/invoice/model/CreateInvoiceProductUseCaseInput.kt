package com.example.project_shelf.app.use_case.invoice.model

data class CreateInvoiceProductUseCaseInput(
    val productId: Long,
    val price: Long?,
    val count: Int?,
)