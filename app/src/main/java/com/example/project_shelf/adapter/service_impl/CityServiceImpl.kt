package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.room.withTransaction
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.adapter.dto.room.CityFtsDto
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.service.CityService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class CityServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : CityService {
    override suspend fun create(name: String, department: String): City {
        Log.d("SERVICE-IMPL", "Creating city with: $name, $department")

        return database.withTransaction {
            // First, store the data.
            val cityId = database.cityDao().insert(
                CityDto(
                    name = name,
                    department = department,
                )
            )

            // Then, store the FTS value.
            database.cityFtsDao().insert(CityFtsDto(cityId = cityId, name = name))

            City(
                id = cityId, name = name, department = department
            )
        }
    }

    override suspend fun count(): Int {
        Log.d("SERVICE-IMPL", "Counting stored cities")
        return database.cityDao().count()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CityModule {
    @Binds
    abstract fun bindService(impl: CityServiceImpl): CityService
}