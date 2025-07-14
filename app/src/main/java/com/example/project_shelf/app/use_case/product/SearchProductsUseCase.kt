package com.example.project_shelf.app.use_case.product

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(private val service: ProductService) {
    fun exec(value: String): Flow<PagingData<Product>> {
        Log.d("USE-CASE", "Searching products with: $value")
        return service.search(value)
    }
}