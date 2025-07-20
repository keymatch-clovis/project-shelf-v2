package com.example.project_shelf.app.use_case.invoice

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Invoice
import com.example.project_shelf.app.service.InvoiceService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInvoicesUseCase @Inject constructor(private val service: InvoiceService) {
    fun exec(): Flow<PagingData<Invoice>> {
        Log.d("USE-CASE", "Getting invoices")
        return service.get()
    }
}