package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.InvoiceDto
import com.example.project_shelf.adapter.dto.room.InvoiceFtsDto
import com.example.project_shelf.adapter.dto.room.InvoiceWithCustomerDto
import com.example.project_shelf.adapter.dto.room.ProductDto

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoice WHERE pending_delete_until IS NULL")
    fun select(): PagingSource<Int, InvoiceDto>

    @Transaction
    @Query("SELECT * FROM invoice WHERE pending_delete_until IS NULL")
    fun selectWithCustomer(): PagingSource<Int, InvoiceWithCustomerDto>

    @Query("SELECT MAX(number) FROM invoice")
    suspend fun getMaxNumber(): Long

    @Query("DELETE FROM invoice")
    suspend fun delete()

    @Query("DELETE FROM invoice WHERE rowid = :id")
    suspend fun delete(id: Long)

    @Insert
    suspend fun insert(dto: InvoiceDto): Long

    @Update
    suspend fun update(dto: ProductDto)
}

@Dao
interface InvoiceFtsDao {
    @Insert
    suspend fun insert(dto: InvoiceFtsDto)

    @Query("DELETE FROM invoice_fts")
    suspend fun delete()

    @Query("DELETE FROM invoice_fts WHERE invoice_id = :invoiceId")
    suspend fun delete(invoiceId: Long)

    @Query(
        """
        SELECT fts.* FROM invoice_fts fts
        JOIN invoice i ON (i.rowid = fts.invoice_id)
        WHERE  
            i.pending_delete_until IS NULL
            AND invoice_fts MATCH :value
    """
    )
    fun match(value: String): PagingSource<Int, InvoiceFtsDto>
}