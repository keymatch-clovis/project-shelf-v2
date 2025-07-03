package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.InvoiceDto
import com.example.project_shelf.adapter.dto.room.InvoiceFtsDto
import com.example.project_shelf.adapter.dto.room.ProductDto

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoice")
    fun select(): PagingSource<Int, InvoiceDto>

    @Query("SELECT MAX(number) FROM invoice")
    suspend fun getCurrentInvoiceNumber(): Long

    @Query("DELETE FROM invoice")
    suspend fun delete()

    @Delete
    suspend fun delete(dto: InvoiceDto)

    @Insert
    suspend fun insert(dto: InvoiceDto): Long

    @Update
    suspend fun update(dto: ProductDto)
}

@Dao
interface InvoiceFtsDao {
    @Insert
    suspend fun insert(dto: InvoiceFtsDto)

    @Query("SELECT * FROM invoice_fts WHERE invoice_fts MATCH :value")
    fun match(value: String): PagingSource<Int, InvoiceFtsDto>
}