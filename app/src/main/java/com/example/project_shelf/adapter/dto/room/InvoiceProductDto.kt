package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Relation
import com.example.project_shelf.app.entity.InvoiceProduct

@Entity(
    tableName = "invoice_product",
    primaryKeys = ["invoice_id", "product_id"],
    foreignKeys = [
        ForeignKey(
            entity = InvoiceDto::class,
            parentColumns = arrayOf("rowid"),
            childColumns = arrayOf("invoice_id"),
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = ProductDto::class,
            parentColumns = arrayOf("rowid"),
            childColumns = arrayOf("product_id"),
            onDelete = ForeignKey.RESTRICT,
        ),
    ]
)
data class InvoiceProductDto(
    /// Primary key
    @ColumnInfo(name = "invoice_id") val invoiceId: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    /// Required fields
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "discount") val discount: String,
)

data class InvoiceProductWithProductDto(
    @Embedded val invoiceProduct: InvoiceProductDto,
    @Relation(
        parentColumn = "rowid",
        entityColumn = "product_id"
    )
    val product: ProductDto
)

fun InvoiceProductDto.toEntity(): InvoiceProduct {
    return InvoiceProduct(
        invoiceId = this.invoiceId,
        productId = this.productId,
        count = this.count,
        price = this.price.toBigInteger(),
        discount = this.discount.toBigInteger(),
    )
}

fun InvoiceProduct.toDto(): InvoiceProductDto {
    return InvoiceProductDto(
        invoiceId = this.invoiceId,
        productId = this.productId,
        count = this.count,
        price = this.price.toString(),
        discount = this.discount.toString(),
    )
}