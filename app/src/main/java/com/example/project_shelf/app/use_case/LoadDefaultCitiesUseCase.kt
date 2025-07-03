package com.example.project_shelf.app.use_case

import android.util.Log
import com.example.project_shelf.app.service.CityService
import java.io.InputStream
import javax.inject.Inject

class LoadDefaultCitiesUseCase @Inject constructor(private val cityService: CityService) {
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
                cityService.create(name = city, department = department)
            }
    }
}