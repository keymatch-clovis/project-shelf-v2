package com.example.project_shelf.app.entity

data class City(
    val id: Long,
    val name: String,
    val department: String,
)

data class CityFilter(
    val id: Long,
    val name: String,
)