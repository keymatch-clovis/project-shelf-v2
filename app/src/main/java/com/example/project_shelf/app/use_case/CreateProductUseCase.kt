package com.example.project_shelf.app.use_case

import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        name: String,
        price: BigDecimal,
        stock: Int,
    ): Product {
        // We are here converting from any value to COP. So, if we need later to change this to
        // any other currency, we can do it here.
        // TODO: We can get the currency from a configuration option or something, but for now we'll
        // leave it hard coded.
        val money = Money.of(CurrencyUnit.of("COP"), price)
        return productService.create(
            name = name.uppercase(),
            price = money.amountMinor,
            stock = stock,
        )
    }
}