package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
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
    @ColumnInfo(name = "remaining_unpaid_balance") val remainingUnpaidBalance: Long,
    /// Relationships
    @ColumnInfo(name = "customer_id") val customerId: Long,
    /// Functional properties
    @ColumnInfo(name = "pending_delete_until") val pendingDeleteUntil: Long? = null,
)

fun InvoiceDto.toEntity() = Invoice(
    id = this.rowId,
    customerId = this.customerId,
    number = this.number,
    date = Date(this.date),
    remainingUnpaidBalance = this.remainingUnpaidBalance,
)

fun Invoice.toDto(): InvoiceDto {
    return InvoiceDto(
        rowId = this.id,
        number = this.number,
        date = this.date.time,
        customerId = this.customerId,
        remainingUnpaidBalance = this.remainingUnpaidBalance,
    )
}

/// Relationships
data class InvoiceWithCustomerDto(
    @Embedded val invoice: InvoiceDto,

    @Relation(
        parentColumn = "customer_id",
        entityColumn = "rowid",
    ) val customer: CustomerDto,
)