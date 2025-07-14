package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.project_shelf.app.entity.City

@Entity(tableName = "city")
data class CityDto(
    /// Primary key
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowId: Long = 0,
    /// Required fields
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "department") val department: String,
)

fun CityDto.toEntity(): City {
    return City(
        id = this.rowId,
        name = this.name,
        department = this.department,
    )
}