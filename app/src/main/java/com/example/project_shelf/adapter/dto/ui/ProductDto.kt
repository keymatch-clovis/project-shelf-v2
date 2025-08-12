package com.example.project_shelf.adapter.dto.ui

import android.icu.util.Currency
import android.os.Parcelable
import com.example.project_shelf.app.entity.Product
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
@Parcelize
data class ProductDto(
    val id: Long,
    val name: String,
    val price: String,
    val formattedPrice: String,
    val stock: String,
) : Parcelable

fun Product.toDto(currency: Currency): ProductDto {
    // FIXME: See if this is correct.
    // As we are storing our currencies as integers, we need to show the correct fraction digits on
    // the UI. We are using Joda Money in the app layer, but here we use the simpler abstraction of
    // currency for the UI.
    val realDefaultPrice: Long = this.defaultPrice / 10.0
        .pow(currency.defaultFractionDigits)
        .toLong()

    return ProductDto(
        id = this.id,
        name = this.name,
        formattedPrice = "${currency.symbol} $realDefaultPrice",
        // NOTE: I feel this looks better, than to see the 0 value.
        price = if (realDefaultPrice == 0L) "" else realDefaultPrice.toString(),
        stock = if (this.stock == 0) "" else this.stock.toString(),
    )
}