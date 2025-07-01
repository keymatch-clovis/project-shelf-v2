package com.example.project_shelf.adapter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Dao
interface CityDao {
    @Query("SELECT COUNT(*) FROM city")
    suspend fun count(): Int

    @Insert
    suspend fun insert(dto: CityDto)
}

@Module
@InstallIn(SingletonComponent::class)
object CityModule {
    @Provides
    fun provideCityDao(database: SqliteDatabase): CityDao {
        return database.cityDao()
    }
}