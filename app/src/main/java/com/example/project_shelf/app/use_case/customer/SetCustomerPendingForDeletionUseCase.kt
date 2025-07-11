package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.service.CustomerService
import com.example.project_shelf.app.use_case.DELETION_TIMEOUT
import java.util.Date
import javax.inject.Inject

class SetCustomerPendingForDeletionUseCase @Inject constructor(private val service: CustomerService) {
    suspend fun exec(id: Long) {
        Log.d("USE-CASE", "Customer[$id]: Setting customer pending for deletion")
        service.setPendingForDeletion(
            id,
            until = Date().time + DELETION_TIMEOUT.inWholeMilliseconds
        )
    }
}