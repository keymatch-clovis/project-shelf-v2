package com.example.project_shelf.app.use_case.product

import android.util.Log
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
        Log.d("USE-CASE", "Creating product with: $name, $price, $stock")

        // As we allow soft deletes in our little app, we need to handle those cases here.
        // Fortunately, the product entity does not meddle with other entities, so we just have to
        // clean that table before doing anything else. We could do a full database clean here, but
        // I think it is a bit unnecessary, as we can address the matters related to the product
        // creation here and only here.
        productService.deletePendingForDeletion()

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