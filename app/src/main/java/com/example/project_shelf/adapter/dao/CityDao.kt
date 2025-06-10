package com.example.project_shelf.adapter.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.project_shelf.adapter.dto.CityDto

@Dao
interface CityDao {
    @Insert
    suspend fun insert(dto: CityDto)
}