package com.example.project_shelf.adapter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.PROPERTY_ID
import com.example.project_shelf.adapter.dto.room.PropertyDto

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property WHERE rowid = $PROPERTY_ID")
    suspend fun select(): PropertyDto

    @Insert
    suspend fun insert(dto: PropertyDto)

    @Update
    suspend fun update(dto: PropertyDto)

    @Query("DELETE FROM property WHERE rowid = $PROPERTY_ID")
    suspend fun delete()
}