package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.example.project_shelf.app.entity.CustomerFilter

@Entity(tableName = "customer_fts")
@Fts4
data class CustomerFtsDto(
    @ColumnInfo(name = "customer_id") val customerId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "businessName") val businessName: String?,
)

fun CustomerFtsDto.toCustomerFilter(): CustomerFilter {
    return CustomerFilter(
        id = this.customerId,
        name = this.name,
        businessName = this.businessName,
    )
}