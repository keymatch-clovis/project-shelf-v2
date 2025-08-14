package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.service.model.CreateProductInput
import org.joda.money.Money
import java.math.BigDecimal
import javax.inject.Inject

data class CreateProductUseCaseInput(
    val name: String,
    val price: BigDecimal?,
    val stock: Int?,
)

class CreateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(input: CreateProductUseCaseInput): Product {
        Log.d("USE-CASE", "Creating product with: $input")

        // As we allow soft deletes in our little app, we need to handle those cases here.
        // We will assume that the products marked for deletion have passed checks and they are
        // ready to be deleted. So we just have to clean that table before doing anything else.
        productService.deletePendingForDeletion()

        val money = Money.of(currencyUnitFromDefaultLocale(), input.price ?: BigDecimal.ZERO)
        return productService.create(
            CreateProductInput(
                name = input.name.uppercase(),
                defaultPrice = money.amountMinorLong,
                stock = input.stock ?: 0,
            )
        )
    }
}