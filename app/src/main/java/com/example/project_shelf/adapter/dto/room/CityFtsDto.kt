package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "city_fts")
@Fts4
data class CityFtsDto(
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "name") val name: String,
)