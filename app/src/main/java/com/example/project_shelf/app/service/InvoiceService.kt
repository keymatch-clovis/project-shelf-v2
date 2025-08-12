package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceDraft
import com.example.project_shelf.app.entity.InvoiceFilter
import com.example.project_shelf.app.entity.InvoicePopulated
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import com.example.project_shelf.app.service.model.CreateInvoiceDraftInput
import com.example.project_shelf.app.service.model.CreateInvoiceInput
import com.example.project_shelf.app.service.model.EditInvoiceDraftInput
import kotlinx.coroutines.flow.Flow

interface InvoiceService {
    /// Search related
    fun get(): Flow<PagingData<Invoice>>
    fun getWithCustomer(): Flow<PagingData<InvoiceWithCustomer>>
    fun getPopulated(): Flow<PagingData<InvoicePopulated>>
    suspend fun getCurrentNumber(): Long
    fun search(value: String): Flow<PagingData<InvoiceFilter>>

    /// Create related
    suspend fun create(input: CreateInvoiceInput): Long

    /// Update related
    suspend fun update()

    /// Delete related
    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()
    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)

    /// Draft related
    suspend fun getDrafts(): List<InvoiceDraft>
    suspend fun createDraft(input: CreateInvoiceDraftInput): Long
    suspend fun editDraft(input: EditInvoiceDraftInput)
    suspend fun deleteDrafts(vararg ids: Long)
}