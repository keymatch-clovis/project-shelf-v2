package com.example.project_shelf.adapter.dto.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import com.example.project_shelf.app.entity.InvoiceFilter

@Entity(tableName = "invoice_fts")
@Fts4
data class InvoiceFtsDto(
    @ColumnInfo(name = "invoice_id") val invoiceId: Long,
    @ColumnInfo(name = "number") val number: Long,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "customer_business_name") val customerBusinessName: String?,
)

fun InvoiceFtsDto.toEntity(): InvoiceFilter {
    return InvoiceFilter(
        id = this.invoiceId,
        number = this.number,
        customerName = this.customerName,
        customerBusinessName = this.customerBusinessName,
    )
}