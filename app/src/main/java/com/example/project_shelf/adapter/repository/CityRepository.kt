package com.example.project_shelf.adapter.repository

import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import java.io.InputStream

interface CityRepository : WithSearch<CityFilterDto> {
    suspend fun loadDefaultCities(stream: InputStream)
}