package com.example.project_shelf.app.use_case.customer

import com.example.project_shelf.app.service.CustomerService
import javax.inject.Inject

class UpdateCustomerUseCase @Inject constructor(private val service: CustomerService) {
    suspend fun exec(
        id: Long,
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String,
    ) {
        return service.update(
            id = id,
            name = name,
            phone = phone,
            address = address,
            cityId = cityId,
            businessName = businessName.ifBlank { null },
        )
    }
}