package com.example.project_shelf.app.entity

data class InvoiceFilter(
    val id: Long,
    val number: Long,
    val customerName: String,
    val customerBusinessName: String?,
)
