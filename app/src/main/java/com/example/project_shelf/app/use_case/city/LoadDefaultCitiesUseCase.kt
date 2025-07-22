package com.example.project_shelf.app.use_case.city

import android.util.Log
import com.example.project_shelf.app.service.CityService
import java.io.InputStream
import javax.inject.Inject

class LoadDefaultCitiesUseCase @Inject constructor(private val cityService: CityService) {
    suspend fun exec(stream: InputStream) {
        Log.d("USE_CASE", "Loading base cities")

        // If we execute this use case, it means we want to restore the default data. In this case
        // the cities. As such, we need to remove the old data, to restore the default one.
        Log.d("USE_CASE", "Deleting old cities data")
        cityService.delete()

        // Get a reader for the stream.
        val reader = stream.bufferedReader()

        reader.lineSequence().filter { it.isNotBlank() }
            // Split all the strings into city, department.
            .map { it.split(",", ignoreCase = false, limit = 2) }
            // Insert the data into the database using room.
            .forEach { (department, city) ->
                Log.d("USE_CASE", "Inserting: $city, $department")
                cityService.create(name = city, department = department)
            }
    }
}