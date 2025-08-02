package com.example.project_shelf.adapter.dto.ui

import android.os.Parcelable
import com.example.project_shelf.app.entity.Customer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CustomerDto(
    val id: Long,
    val name: String,
    val phone: String,
    val address: String?,
    val businessName: String?,
) : Parcelable

fun Customer.toDto(): CustomerDto {
    return CustomerDto(
        id = this.id,
        name = this.name,
        phone = this.phone,
        address = this.address,
        businessName = this.businessName,
    )
}