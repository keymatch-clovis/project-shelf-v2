package com.example.project_shelf.adapter.dto.ui

import android.icu.util.Currency
import com.example.project_shelf.app.entity.Product
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val price: String,
    val formattedPrice: String,
    val stock: String,
)

fun Product.toDto(currency: Currency): ProductDto {
    // As we are storing our currencies as integers, we need to show the correct fraction digits on
    // the UI. We are using Joda Money in the app layer, but here we use the simpler abstraction of
    // currency for the UI.
    val realDefaultPrice: BigDecimal =
        this.defaultPrice / BigDecimal.TEN.pow(currency.defaultFractionDigits)

    return ProductDto(
        id = this.id,
        name = this.name,
        formattedPrice = "${currency.symbol} $realDefaultPrice",
        // NOTE: I feel this looks better, than to see the 0 value.
        price = if (realDefaultPrice == BigDecimal.ZERO) "" else realDefaultPrice.toString(),
        stock = if (this.stock == 0) "" else this.stock.toString(),
    )
}