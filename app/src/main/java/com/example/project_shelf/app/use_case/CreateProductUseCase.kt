package com.example.project_shelf.app.use_case

import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import java.math.BigDecimal
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val productService: ProductService) {
    suspend fun exec(
        name: String,
        price: String,
        count: String,
    ) {
        // NOTE: We assume the price that gets here is correctly formatted.
        var realPrice = (if (price.isBlank()) BigDecimal.ZERO else price.toBigDecimal())
        // We are here converting from any value to COP. So, if we need later to change this to
        // any other currency, we can do it here.
        realPrice *= BigDecimal(100)
        val realCount = if (count.isBlank()) 0 else count.toInt()

        return productService.createProduct(
            Product(
                name = name,
                // We are here converting from any value to COP. So, if we need later to change this to
                // any other currency, we can do it here.
                price = realPrice.toBigInteger(),
                count = realCount,
            )
        )
    }
}