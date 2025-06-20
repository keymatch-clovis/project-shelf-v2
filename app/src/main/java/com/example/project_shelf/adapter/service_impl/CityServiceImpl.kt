package com.example.project_shelf.adapter.service_impl

import com.example.project_shelf.adapter.dao.CityDao
import com.example.project_shelf.adapter.dto.CityDto
import com.example.project_shelf.app.entity.City
import com.example.project_shelf.app.service.CityService

class CityServiceImpl(private val dao: CityDao) : CityService {
    override suspend fun create(city: City) {
        return dao.insert(
            CityDto(
                uid = 0,
                name = city.name,
                department = city.department
            )
        )
    }
}