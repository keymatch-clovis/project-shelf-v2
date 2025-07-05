package com.example.project_shelf.app.use_case.product

import com.example.project_shelf.app.service.CustomerService
import javax.inject.Inject

class CreateCustomerUseCase @Inject constructor(private val customerService: CustomerService) {
    suspend fun exec(
        name: String,
        phone: String,
        address: String,
        cityId: Long,
        businessName: String?,
    ) {
        // NOTE: We assume all the data entering here is completely sanitized.
        return customerService.create(name, phone, address, cityId, businessName)
    }
}