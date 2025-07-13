package com.example.project_shelf.adapter.dto.ui

import com.example.project_shelf.app.entity.CityFilter

data class CityFilterDto(
    val name: String,
)

fun CityFilter.toDto(): CityFilterDto {
    return CityFilterDto(name = this.name)
}