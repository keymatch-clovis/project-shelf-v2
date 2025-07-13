package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface CityRepository {
    suspend fun loadDefaultCities(stream: InputStream)
    suspend fun hasLoadedDefaultCities(): Boolean

    fun search(value: String): Flow<PagingData<CityFilterDto>>
}