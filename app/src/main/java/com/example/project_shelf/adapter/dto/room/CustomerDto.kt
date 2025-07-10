package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.project_shelf.app.entity.Customer

@Entity(
    tableName = "customer",
    foreignKeys = [ForeignKey(
        entity = CityDto::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("city_id"),
    )]
)
data class CustomerDto(
    /// Primary key
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowId: Long = 0,
    /// Required fields
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "address") val address: String,
    /// Optional fields
    @ColumnInfo(name = "business_name") val businessName: String?,
    /// Relationships
    @ColumnInfo(name = "city_id") val cityId: Long,
    /// Functional properties
    @ColumnInfo(name = "pending_delete_until") val pendingDeleteUntil: Long? = null,
)

fun CustomerDto.toEntity(): Customer {
    return Customer(
        id = this.rowId,
        cityId = this.cityId,
        name = this.name,
        phone = this.phone,
        address = this.address,
        businessName = this.businessName,
    )
}

fun Customer.toDto(): CustomerDto {
    return CustomerDto(
        rowId = this.id,
        name = this.name,
        phone = this.phone,
        address = this.address,
        businessName = this.businessName,
        cityId = this.cityId,
    )
}