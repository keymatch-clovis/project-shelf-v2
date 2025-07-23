package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.entity.CityFilter
import kotlinx.coroutines.flow.Flow

interface CityService {
    fun search(value: String): Flow<PagingData<CityFilter>>

    suspend fun delete()
    suspend fun create(name: String, department: String): City
    suspend fun count(): Int
}