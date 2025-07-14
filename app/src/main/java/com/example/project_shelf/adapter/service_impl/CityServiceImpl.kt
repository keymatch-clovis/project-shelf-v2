package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.adapter.dto.room.CityFtsDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.entity.CityFilter
import com.example.project_shelf.app.service.CityService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : CityService {
    override fun search(value: String): Flow<PagingData<City>> {
        Log.d("SERVICE-IMPL", "Searching cities with: $value")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.cityFtsDao().match("$value*")
        }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

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