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

    // Invoice Category
    INVOICE("main.invoice"),

    // Config Category
    CONFIG("main.config"),
}