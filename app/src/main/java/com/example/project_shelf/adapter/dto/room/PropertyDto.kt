package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val PROPERTY_ID = 1L

@Entity(tableName = "property")
data class PropertyDto(
    /// Primary key
    // NOTE:
    //  We are using this table to store all the app properties that are a bit more delicate as a
    //  BSON. So, this table will only have one row, and will always be the same ID.
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "rowid") val rowId: Long = PROPERTY_ID,

    /// Required fields
    @Suppress("ArrayInDataClass") @ColumnInfo(
        name = "bson", typeAffinity = ColumnInfo.BLOB
    ) val bson: ByteArray
)