package com.example.project_shelf.framework.ui

import com.example.project_shelf.R

enum class MainDestination(
    val route: String,
    val iconVectorResource: Int,
    val labelStringResource: Int,
) {
    /// Product Category
    PRODUCT(
        route = "main.product",
        iconVectorResource = R.drawable.resource_package,
        labelStringResource = R.string.products,
    ),

    /// Client Category
    CUSTOMER(
        route = "main.client",
        iconVectorResource = R.drawable.users,
        labelStringResource = R.string.customers,
    ),

    /// Invoice Category
    INVOICE(
        route = "main.invoice",
        iconVectorResource = R.drawable.receipt_text,
        labelStringResource = R.string.invoices,
    ),

    /// Config Category
    CONFIG(
        route = "main.config",
        iconVectorResource = R.drawable.settings,
        labelStringResource = R.string.configuration,
    ),
}

enum class Destination(
    val route: String,
) {
    /// Loading Screen
    LOADING("loading"),

    /// Client Category
    CREATE_CUSTOMER("main.client.create"),

    /// Product Category
    CREATE_PRODUCT("main.product.create"),

    /// Invoice Category
    INVOICE_LIST("main.invoice.list"),
    CREATE_INVOICE("main.invoice.create"),
    INVOICE_DRAFT_LIST("main.invoice.saved"),
}
