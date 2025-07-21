package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.InvoiceWithCustomer
import com.example.project_shelf.app.service.InvoiceService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInvoicesWithCustomersUseCase @Inject constructor(private val service: InvoiceService) {
    fun exec(): Flow<PagingData<InvoiceWithCustomer>> {
        Log.d("USE-CASE", "Getting invoices with customers")
        return service.getWithCustomer()
    }
}