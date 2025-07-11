package com.example.project_shelf.app.use_case.customer

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.CustomerFilter
import com.example.project_shelf.app.service.CustomerService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCustomersUseCase @Inject constructor(private val service: CustomerService) {
    fun exec(value: String): Flow<PagingData<CustomerFilter>> {
        Log.d("USE-CASE", "Searching customers with: $value")
        return service.search(value)
    }
}