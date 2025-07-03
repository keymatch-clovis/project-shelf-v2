package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.InvoiceProductDto

@Dao
interface InvoiceProductDao {
    @Query("SELECT * FROM invoice_product WHERE product_id = :id")
    fun selectByProduct(id: Long): PagingSource<Int, InvoiceProductDto>

    @Query("SELECT * FROM invoice_product WHERE product_id = :id")
    fun selectByInvoice(id: Long): PagingSource<Int, InvoiceProductDto>

    @Query("DELETE FROM invoice_product WHERE invoice_id = :invoiceId")
    suspend fun deleteProducts(invoiceId: Long)
}