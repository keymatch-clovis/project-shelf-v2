package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.CityFilter

data class CityFilterDto(
    val id: Long,
    val name: String,
    val department: String,
)

fun CityFilter.toDto() = CityFilterDto(
    id = this.id,
    name = this.name,
    department = this.department,
)