package com.example.project_shelf.app.use_case.customer

import android.util.Log
import com.example.project_shelf.app.entity.Customer
import com.example.project_shelf.app.service.CustomerService
import javax.inject.Inject

class CreateCustomerUseCase @Inject constructor(private val customerService: CustomerService) {
    suspend fun exec(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String,
    ): Customer {
        Log.d("USE-CASE", "Creating customer with: $name, $phone, $address, $cityId, $businessName")

        // As we allow soft deletes in our little app, we need to handle those cases here.
        // The customer entity has dependencies with the invoices, so deleting customers before
        // creating a new one might fail. This is unexpected behavior, so we can accept that here.
        customerService.deletePendingForDeletion()

        return customerService.create(
            name = name,
            phone = phone,
            address = address,
            cityId = cityId,
            businessName = businessName.ifBlank { null },
        )
    }
}