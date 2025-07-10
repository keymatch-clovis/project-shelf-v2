package com.example.project_shelf.app.use_case.product

import android.util.Log
import androidx.paging.PagingData
import com.example.project_shelf.app.entity.ProductFilter
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class FindUseCase @Inject constructor(private val productService: ProductService) {
    fun exec(name: String): Flow<PagingData<ProductFilter>> {
        Log.d("USE-CASE", "Searching products with: $name")
        return productService.search(name)
    }
}