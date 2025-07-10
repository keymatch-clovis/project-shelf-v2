package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.project_shelf.app.entity.ProductFilter

@Fts4
@Entity(tableName = "product_fts")
data class ProductFtsDto(
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "name") val name: String,
)

fun ProductFtsDto.toProductFilter(): ProductFilter {
    return ProductFilter(
        id = this.productId,
        name = this.name,
    )
}