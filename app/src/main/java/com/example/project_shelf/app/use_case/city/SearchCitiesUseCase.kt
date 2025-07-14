package com.example.project_shelf.app.use_case.city

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.service.CityService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(private val service: CityService) {
    fun exec(value: String): Flow<PagingData<City>> {
        Log.d("USE-CASE", "Searching cities with: $value")
        return service.search(value)
    }
}