package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.service.CustomerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateCustomerUseCase @Inject constructor(private val customerService: CustomerService) {
    suspend fun exec(
        name: String,
        phone: String,
        cityId: Long,
        businessName: String?,
        address: String?,
    ): Customer = withContext(Dispatchers.IO) {
        Log.d("USE-CASE", "Creating customer with: $name, $phone, $address, $cityId, $businessName")

        // As we allow soft deletes in our little app, we need to handle those cases here.
        // The customer entity has dependencies with the invoices, so deleting customers before
        // creating a new one might fail. This is unexpected behavior, so we can accept that here.
        customerService.deletePendingForDeletion()

        customerService.create(
            name = name,
            phone = phone,
            address = TODO("See: #4"),
            cityId = cityId,
            businessName = businessName,
        )
    }
}