package com.example.project_shelf.lib.service

import com.example.project_shelf.lib.entity.City

interface CityService {
    suspend fun create(city: City)
}