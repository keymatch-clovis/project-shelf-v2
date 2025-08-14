package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.app.entity.Product
import org.joda.money.Money
import java.util.Date

@Entity(tableName = "product")
data class ProductDto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "default_price") val defaultPrice: Long,
    @ColumnInfo(name = "stock") val stock: Int,
    /// Functional properties
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
    @ColumnInfo(name = "pending_delete_until") val pendingDeleteUntil: Date? = null,
)

fun ProductDto.toEntity(): Product {
    return Product(
        id = this.rowId,
        name = this.name,
        defaultPrice = Money.ofMinor(currencyUnitFromDefaultLocale(), this.defaultPrice),
        stock = this.stock,
    )
}