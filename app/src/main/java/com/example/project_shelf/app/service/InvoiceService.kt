package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceDraft
import com.example.project_shelf.app.entity.InvoiceFilter
import com.example.project_shelf.app.entity.InvoicePopulated
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

interface InvoiceService {
    data class ProductParam(
        val id: Long,
        val count: Int,
        val price: Long,
        val discount: Long,
    )

    fun get(): Flow<PagingData<Invoice>>
    fun getWithCustomer(): Flow<PagingData<InvoiceWithCustomer>>
    fun getPopulated(): Flow<PagingData<InvoicePopulated>>

    suspend fun getCurrentNumber(): Long

    fun search(value: String): Flow<PagingData<InvoiceFilter>>

    suspend fun create(
        number: Long,
        customerId: Long,
        date: Date,
        products: List<ProductParam>,
        discount: BigDecimal?,
    ): Long

    suspend fun update()

    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()

    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)

    suspend fun createDraft(
        date: Date,
        products: List<ProductParam>,
        remainingUnpaidBalance: Long,
        customerId: Long?,
    ): Long

    suspend fun saveDraft(
        draftId: Long,
        date: Date,
        products: List<ProductParam>,
        remainingUnpaidBalance: Long,
        customerId: Long?,
    )

    suspend fun getDrafts(): List<InvoiceDraft>
}