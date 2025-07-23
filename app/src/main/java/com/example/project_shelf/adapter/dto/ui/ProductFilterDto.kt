package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.ProductFilter

data class ProductFilterDto(
    val id: Long,
    val name: String,
)

fun ProductFilter.toDto() = ProductFilterDto(
    id = this.id,
    name = this.name,
)