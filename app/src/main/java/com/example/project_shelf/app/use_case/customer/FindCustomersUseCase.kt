package com.example.project_shelf.app.use_case.customer

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.service.CustomerService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindCustomersUseCase @Inject constructor(private val service: CustomerService) {
    fun exec(): Flow<PagingData<Customer>> {
        return service.find()
    }
}