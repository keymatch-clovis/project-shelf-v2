package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.entity.InvoiceFilter
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface InvoiceService {
    fun getInvoices(): Flow<PagingData<Invoice>>
    fun getInvoices(searchParam: String): Flow<PagingData<InvoiceFilter>>

    suspend fun create(customer: Customer, discount: BigInteger = BigInteger.ZERO)
    suspend fun delete(invoice: Invoice)
}