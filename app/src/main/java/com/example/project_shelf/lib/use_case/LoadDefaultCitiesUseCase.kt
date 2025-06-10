package com.example.project_shelf.lib.use_case

import android.util.Log
import com.example.project_shelf.lib.entity.City
import com.example.project_shelf.lib.service.CityService
import java.io.InputStream

class LoadDefaultCitiesUseCase(private val cityService: CityService) {
    suspend fun exec(stream: InputStream) {
        // First, get a reader for the stream.
        val reader = stream.bufferedReader()

        Log.d("USE_CASE", "Loading base cities")
        reader.lineSequence().filter { it.isNotBlank() }
            // Split all the strings into city, department.
            .map { it.split(",", ignoreCase = false, limit = 2) }
            // Insert the data into the database using room.
            .forEach { (city, department) ->
                Log.d("USE_CASE", "Inserting: $city, $department")
                cityService.create(City(name = city, department = department))
            }
    }
}