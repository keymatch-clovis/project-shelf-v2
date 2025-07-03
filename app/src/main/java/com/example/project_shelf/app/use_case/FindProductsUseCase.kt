package com.example.project_shelf.app.use_case

import androidx.paging.PagingData
import com.example.project_shelf.app.entity.ProductFilter
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindProductsUseCase @Inject constructor(private val productService: ProductService) {
    fun exec(name: String): Flow<PagingData<ProductFilter>> {
        // NOTE: We are not doing anything with the getting of the products. Just returning them.
        return productService.getProducts(name)
    }
}