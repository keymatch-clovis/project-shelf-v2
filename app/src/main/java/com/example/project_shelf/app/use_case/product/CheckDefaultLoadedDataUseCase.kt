package com.example.project_shelf.app.use_case.product

import com.example.project_shelf.app.service.CityService
import javax.inject.Inject

class CheckDefaultLoadedDataUseCase @Inject constructor(private val cityService: CityService) {
    suspend fun exec(): Boolean {
        // NOTE: For now, the default data are just the cities, so check if the collection has any
        // kind of data. This is not entirely correct, but works for now.
        val count = cityService.count()
        return count > 0
    }
}