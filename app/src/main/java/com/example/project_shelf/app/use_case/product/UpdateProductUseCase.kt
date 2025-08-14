package com.example.project_shelf.app.use_case.product

import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.service.model.UpdateProductInput
import org.joda.money.Money
import java.math.BigDecimal
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        id: Long,
        name: String,
        price: BigDecimal?,
        stock: Int?,
    ): Product {
        // As we allow soft deletes in our little app, we need to handle those cases here.
        // We will assume that the products marked for deletion have passed checks and they are
        // ready to be deleted. So we just have to clean that table before doing anything else.
        productService.deletePendingForDeletion()

        val money = Money.of(currencyUnitFromDefaultLocale(), price ?: BigDecimal.ZERO)

        return productService.update(
            UpdateProductInput(
                id = id,
                name = name.uppercase(),
                price = money.amountMinorLong,
                stock = stock ?: 0,
            )
        )
    }
}