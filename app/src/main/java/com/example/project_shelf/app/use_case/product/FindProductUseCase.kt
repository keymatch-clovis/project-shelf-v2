package com.example.project_shelf.app.use_case.product

import android.util.Log
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.common.Id
import javax.inject.Inject

class FindProductUseCase @Inject constructor(private val service: ProductService) {
    suspend fun exec(id: Id): Product {
        Log.d("USE-CASE", "Product[$id]: finding product with ID")
        return service.findById(id)
    }
}