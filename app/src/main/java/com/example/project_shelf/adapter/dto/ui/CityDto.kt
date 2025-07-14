package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.City

data class CityDto(
    val id: Long,
    val name: String,
    val department: String,
)

fun City.toDto(): CityDto {
    return CityDto(
        id = this.id,
        name = this.name,
        department = this.department,
    )
}