package com.example.project_shelf.adapter.dao

import android.content.Context
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.framework.room.ShelfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideCityDao(@ApplicationContext context: Context): CityDao {
        return ShelfDatabase.getInstance(context).database.cityDao()
    }
}