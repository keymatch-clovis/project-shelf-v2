package com.example.project_shelf.adapter.repository

import com.example.project_shelf.adapter.dto.data_store.SavedInvoiceDto
import kotlinx.coroutines.flow.Flow

interface SavedInvoicesRepository {
    fun get(): Flow<List<SavedInvoiceDto>>
}