package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.CustomerFilter
import com.example.project_shelf.common.Id
import kotlinx.coroutines.flow.Flow

interface CustomerService {
    fun get(): Flow<PagingData<Customer>>

    fun search(value: String): Flow<PagingData<CustomerFilter>>
    suspend fun search(id: Id): Customer?

    suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    ): Customer

    suspend fun update(
        id: Long,
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    )

    suspend fun delete()
    suspend fun delete(id: Long)
    suspend fun deletePendingForDeletion()

    suspend fun setPendingForDeletion(id: Long, until: Long)
    suspend fun unsetPendingForDeletion(id: Long)
}