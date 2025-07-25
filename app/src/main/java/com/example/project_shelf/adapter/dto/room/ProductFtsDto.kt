package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.example.project_shelf.app.entity.ProductFilter

@Fts4
@Entity(tableName = "product_fts")
data class ProductFtsDto(
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "name") val name: String,
)

fun ProductFtsDto.toEntity(): ProductFilter {
    return ProductFilter(
        id = this.productId,
        name = this.name,
    )
}