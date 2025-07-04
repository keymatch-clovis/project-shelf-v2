package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.project_shelf.app.entity.Product

@Entity(
    tableName = "product",
    indices = [Index(value = ["name"], unique = true)],
)
data class ProductDto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "default_price") val defaultPrice: String,
    @ColumnInfo(name = "stock") val stock: Int,
)

fun ProductDto.toEntity(): Product {
    return Product(
        id = this.rowId,
        name = this.name,
        defaultPrice = this.defaultPrice.toBigDecimal(),
        stock = this.stock,
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        rowId = this.id,
        name = this.name,
        defaultPrice = this.defaultPrice.toString(),
        stock = this.stock,
    )
}