package com.example.project_shelf.app.service

import com.example.project_shelf.app.entity.City

interface CityService {
    suspend fun create(name: String, department: String): City
    suspend fun count(): Int
}