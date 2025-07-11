package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.service.CustomerService
import com.example.project_shelf.app.use_case.DELETION_TIMEOUT
import java.util.Date
import javax.inject.Inject

class UnsetCustomerPendingForDeletionUseCase @Inject constructor(private val service: CustomerService) {
    suspend fun exec(id: Long) {
        Log.d("USE-CASE", "Customer[$id]: Unsetting customer pending for deletion")
        service.unsetPendingForDeletion(id)
    }
}