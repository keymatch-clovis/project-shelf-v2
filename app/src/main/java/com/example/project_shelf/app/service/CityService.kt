package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.City
import kotlinx.coroutines.flow.Flow

interface CityService {
    fun search(value: String): Flow<PagingData<City>>

    suspend fun create(name: String, department: String): City
    suspend fun count(): Int
}