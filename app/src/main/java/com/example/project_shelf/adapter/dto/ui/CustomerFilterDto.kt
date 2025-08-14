package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.CustomerFilter

data class CustomerFilterDto(
    val id: Long,
    val name: String,
    val businessName: String?,
)

fun CustomerFilter.toDto(): CustomerFilterDto {
    return CustomerFilterDto(
        id = this.id,
        name = this.name,
        businessName = this.businessName,
    )
}