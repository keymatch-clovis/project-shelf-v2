package com.example.project_shelf.adapter.repository

import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import kotlinx.coroutines.flow.Flow

interface CustomerRepository : WithSearch<CustomerFilterDto> {
    fun find(): Flow<PagingData<CustomerDto>>

    suspend fun update(
        id: Long,
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String
    )

    suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String
    ): CustomerDto

    suspend fun setPendingForDeletion(id: Long)
    suspend fun unsetPendingForDeletion(id: Long)

    suspend fun deleteAll()
}