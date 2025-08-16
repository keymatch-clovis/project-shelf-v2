package com.example.project_shelf.framework.ui

import com.example.project_shelf.R

enum class MainDestination(
    val route: String,
    val startDestination: String,
    val iconVectorResource: Int,
    val labelStringResource: Int,
) {
    /// Product Category
    PRODUCT(
        route = "main.product",
        startDestination = Destination.PRODUCT_LIST.route,
        iconVectorResource = R.drawable.resource_package,
        labelStringResource = R.string.products,
    ),

    /// Client Category
    CUSTOMER(
        route = "main.customer",
        startDestination = Destination.CUSTOMER_LIST.route,
        iconVectorResource = R.drawable.users,
        labelStringResource = R.string.customers,
    ),

    /// Invoice Category
    INVOICE(
        route = "main.invoice",
        startDestination = Destination.INVOICE_LIST.route,
        iconVectorResource = R.drawable.receipt_text,
        labelStringResource = R.string.invoices,
    ),

    /// Config Category
    CONFIG(
        route = "main.config",
        startDestination = Destination.CONFIG_MENU.route,
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
    CUSTOMER_LIST("main.customer.list"),
    CUSTOMER_CREATE("main.customer.create"),
    CUSTOMER_EDIT("main.customer.edit"),

    /// Product Category
    PRODUCT_LIST("main.product.list"),
    PRODUCT_CREATE("main.product.create"),
    PRODUCT_EDIT("main.product.edit"),

    /// Invoice Category
    INVOICE_LIST("main.invoice.list"),
    INVOICE_CREATE("main.invoice.create"),
    INVOICE_DRAFT_LIST("main.invoice.saved"),

    /// Config category
    CONFIG_MENU("main.config.menu"),
}
