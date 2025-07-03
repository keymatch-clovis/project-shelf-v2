package com.example.project_shelf.app.entity

data class Customer(
    val id: Long,
    val cityId: Long,

    val name: String,
    val phone: String,
    val address: String,
    val businessName: String?,
)

data class CustomerFilter(
    val id: Long,
    val name: String,
    val businessName: String?,
)