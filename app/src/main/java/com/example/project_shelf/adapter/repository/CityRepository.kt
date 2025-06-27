package com.example.project_shelf.adapter.repository

import java.io.InputStream

interface CityRepository {
    suspend fun loadDefaultCities(stream: InputStream)
    suspend fun hasLoadedDefaultCities(): Boolean
}