package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.CityDto
import com.example.project_shelf.adapter.dto.room.CityFtsDto

@Dao
interface CityDao {
    @Query("SELECT COUNT(*) FROM city")
    suspend fun count(): Int

    @Insert
    suspend fun insert(dto: CityDto): Long

    @Query("DELETE FROM city")
    suspend fun delete()
}

@Dao
interface CityFtsDao {
    @Insert
    suspend fun insert(dto: CityFtsDto)

    @Query("DELETE FROM city_fts")
    suspend fun delete()

    @Query(
        """
        SELECT e.* FROM city_fts fts
        JOIN city e ON (e.rowid = fts.city_id)
        WHERE city_fts MATCH :value
    """
    )
    fun match(value: String): PagingSource<Int, CityDto>
}