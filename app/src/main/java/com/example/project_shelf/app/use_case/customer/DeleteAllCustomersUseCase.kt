package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.service.CustomerService
import javax.inject.Inject

class DeleteAllCustomersUseCase @Inject constructor(private val service: CustomerService) {
    suspend fun exec() {
        Log.d("USE-CASE", "Deleting all customers")
        service.delete()
    }
}