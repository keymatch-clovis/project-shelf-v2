package com.example.project_shelf.framework.ui

enum class Destination(
    val path: String,
) {
    /// Loading Screen
    LOADING("loading"),

    /// Main Screen
    MAIN("main"),

    // Client Category
    CLIENT("main.client"),

    // Product Category
    PRODUCT("main.product"),
    CREATE_PRODUCT("main.product.create"),

    // Invoice Category
    INVOICE("main.invoice"),

    // Config Category
    CONFIG("main.config"),
}