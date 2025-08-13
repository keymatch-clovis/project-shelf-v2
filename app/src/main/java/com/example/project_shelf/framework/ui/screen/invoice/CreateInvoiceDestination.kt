package com.example.project_shelf.framework.ui.screen.invoice

import com.example.project_shelf.R

enum class CreateInvoiceDestination(
    val route: String,
    val labelStringResource: Int,
) {
    DETAILS(
        route = "create.invoice.details",
        labelStringResource = R.string.details,
    ),
    PRODUCTS(
        route = "create.invoice.products",
        labelStringResource = R.string.products,
    ),
}