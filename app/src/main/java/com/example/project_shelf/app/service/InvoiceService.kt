package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceFilter
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface InvoiceService {
    fun get(): Flow<PagingData<Invoice>>

    fun search(value: String): Flow<PagingData<Invoice>>

    suspend fun create(customer: Customer, discount: BigInteger = BigInteger.ZERO)
    suspend fun update()

    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()

    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)
}