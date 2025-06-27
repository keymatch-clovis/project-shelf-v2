package com.example.project_shelf.adapter.service_impl

import com.example.project_shelf.adapter.dao.CityDao
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.service.CityService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class CityServiceImpl @Inject constructor(private val dao: CityDao) : CityService {
    override suspend fun create(city: City) {
        return dao.insert(
            CityDto(
                uid = 0,
                name = city.name,
                department = city.department
            )
        )
    }

    override suspend fun count(): Int {
        return dao.count()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CityModule {
    @Binds
    abstract fun bindRepository(impl: CityServiceImpl): CityService
}