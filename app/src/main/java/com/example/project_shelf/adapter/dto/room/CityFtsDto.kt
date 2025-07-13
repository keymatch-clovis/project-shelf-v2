package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.example.project_shelf.app.entity.CityFilter

@Fts4
@Entity(tableName = "city_fts")
data class CityFtsDto(
    @ColumnInfo(name = "city_id") val cityId: Long,
    @ColumnInfo(name = "name") val name: String,
)

fun CityFtsDto.toEntity(): CityFilter {
    return CityFilter(
        id = this.cityId,
        name = this.name,
    )
}