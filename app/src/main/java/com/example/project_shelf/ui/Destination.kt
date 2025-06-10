package com.example.project_shelf.ui

enum class Destination(
    val path: String,
) {
    /// Client Category
    CLIENT("client"),
    CLIENT_LIST("client.list"),

    /// Product Category
    PRODUCT("product"),
    PRODUCT_LIST("product.list"),

    /// Invoice Category
    INVOICE("invoice"),
    INVOICE_LIST("invoice.list"),

    /// Config Category
    CONFIG("config"),
    CONFIG_LIST("config.list"),
}