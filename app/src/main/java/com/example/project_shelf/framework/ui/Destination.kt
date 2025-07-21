package com.example.project_shelf.framework.ui

enum class Destination(
    val path: String,
) {
    /// Loading Screen
    LOADING("loading"),

    /// Main Screen
    MAIN("main"),

    // Client Category
    CUSTOMER("main.client"),
    CREATE_CUSTOMER("main.client.create"),

    // Product Category
    PRODUCT("main.product"),
    CREATE_PRODUCT("main.product.create"),

    // Invoice Category
    INVOICE("main.invoice"),
    CREATE_INVOICE("main.invoice.create"),

    // Config Category
    CONFIG("main.config"),
}