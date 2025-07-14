package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun find(): Flow<PagingData<CustomerDto>>
    fun search(value: String): Flow<PagingData<CustomerDto>>

    suspend fun update(
        id: Long,
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?
    )

    suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?
    )

    suspend fun setPendingForDeletion(id: Long)
    suspend fun unsetPendingForDeletion(id: Long)

    suspend fun deleteAll()
}