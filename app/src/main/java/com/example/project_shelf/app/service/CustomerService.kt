package com.example.project_shelf.app.service

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.entity.CustomerFilter
import kotlinx.coroutines.flow.Flow

interface CustomerService {
    fun getCustomers(): Flow<PagingData<Customer>>
    fun getCustomers(searchValue: String): Flow<PagingData<CustomerFilter>>

    suspend fun create(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    )

    suspend fun removeAll()
    suspend fun remove(customer: Customer)
    suspend fun update(customer: Customer)
}