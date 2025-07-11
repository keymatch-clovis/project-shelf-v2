package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.CustomerFilter

data class CustomerFilterDto(
    val name: String,
    val businessName: String?,
)

fun CustomerFilter.toDto(): CustomerFilterDto {
    return CustomerFilterDto(
        name = this.name,
        businessName = this.businessName,
    )
}