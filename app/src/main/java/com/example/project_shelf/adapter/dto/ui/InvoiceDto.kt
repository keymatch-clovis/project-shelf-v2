package com.example.project_shelf.adapter.dto.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class InvoiceDto(
    val id: Long,
    val number: Long,
    val date: Long,
    val discount: String,
) : Parcelable

@Serializable
@Parcelize
data class InvoiceWithCustomerDto(
    val invoice: InvoiceDto,
    val customer: CustomerDto,
) : Parcelable
