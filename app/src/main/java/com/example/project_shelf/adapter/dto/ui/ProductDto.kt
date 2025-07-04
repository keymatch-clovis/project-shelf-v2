package com.example.project_shelf.adapter.dto.ui

import android.icu.util.Currency
import com.example.project_shelf.app.entity.Product
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val realDefaultPrice: String,
    val formattedDefaultPrice: String,
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
        realDefaultPrice = realDefaultPrice.toString(),
        formattedDefaultPrice = "${currency.symbol} $realDefaultPrice",
        stock = this.stock.toString(),
    )
}