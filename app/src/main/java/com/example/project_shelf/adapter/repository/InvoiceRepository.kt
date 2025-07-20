package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDto
import com.example.project_shelf.adapter.dto.ui.ProductDto
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun get(): Flow<PagingData<InvoiceDto>>

    fun search(value: String): Flow<PagingData<InvoiceDto>>

    suspend fun update(dto: InvoiceDto): InvoiceDto
    suspend fun create(customer: CustomerDto, products: List<ProductDto>): InvoiceDto
}

