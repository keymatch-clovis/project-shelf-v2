package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.project_shelf.app.entity.Invoice
import java.util.Date

@Entity(
    tableName = "invoice",
    indices = [Index(value = ["number", "customer_id"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = CustomerDto::class,
        parentColumns = arrayOf("rowid"),
        childColumns = arrayOf("customer_id"),
        onDelete = ForeignKey.RESTRICT,
    )]
)
data class InvoiceDto(
    /// Primary key
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowId: Long = 0,
    /// Required fields
    @ColumnInfo(name = "number") val number: Long,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "discount") val discount: String,
    /// Relationships
    @ColumnInfo(name = "customer_id") val customerId: Long,
)

fun InvoiceDto.toInvoice(): Invoice {
    return Invoice(
        id = this.rowId,
        customerId = this.customerId,
        number = this.number,
        date = Date(this.date),
        discount = this.discount.toBigInteger(),
    )
}

fun Invoice.toDto(): InvoiceDto {
    return InvoiceDto(
        rowId = this.id,
        number = this.number,
        date = this.date.time,
        discount = this.discount.toString(),
        customerId = this.customerId,
    )
}
