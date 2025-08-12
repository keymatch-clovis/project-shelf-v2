package com.example.project_shelf.app.use_case.product

import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.service.model.UpdateProductInput
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.BigDecimal
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        id: Long,
        name: String,
        price: Long,
        stock: Int,
    ): Product {
        // We are here converting from any value to COP. So, if we need later to change this to
        // any other currency, we can do it here.
        // TODO: We can get the currency from a configuration option or something, but for now we'll
        // leave it hard coded.
        val money = Money.ofMinor(CurrencyUnit.of("COP"), price)
        return productService.update(
            UpdateProductInput(
                id = id,
                name = name.uppercase(),
                price = money.amountMinorLong,
                stock = stock,
            )
        )
    }
}