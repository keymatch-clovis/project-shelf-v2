package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.service.CustomerService
import com.example.project_shelf.common.Id
import javax.inject.Inject

class SearchCustomerUseCase @Inject constructor(private val service: CustomerService) {
    suspend fun exec(id: Id): Customer? {
        Log.d("USE-CASE", "Searching customer with ID: $id")
        return service.search(id)
    }
}