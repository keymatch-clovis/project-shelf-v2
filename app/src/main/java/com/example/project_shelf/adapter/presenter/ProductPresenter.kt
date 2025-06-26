package com.example.project_shelf.adapter.presenter

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.ProductUiState
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.use_case.CreateProductUseCase
import com.example.project_shelf.app.use_case.GetProductsUseCase
import com.example.project_shelf.app.use_case.RemoveAllProductsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger
import javax.inject.Inject

class ProductPresenter @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
) : ProductRepository {
    override fun getProducts(): Flow<PagingData<ProductUiState>> {
        return getProductsUseCase.exec().map {
            it.map { product ->
                ProductUiState(
                    name = product.name,
                    price = product.price.toString(),
                    count = product.count.toString()
                )
            }
        }
    }

    override suspend fun createProduct(product: ProductUiState) {
        Log.d("PRODUCT-PRESENTER", "Creating product with: $product")

        return createProductUseCase.exec(
            name = product.name,
            price = product.price,
            count = product.count,
        )
    }

    override suspend fun removeAll() {
        Log.d("PRODUCT-PRESENTER", "Removing all products")
        removeAllProductsUseCase.exec()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun bindProductService(presenter: ProductPresenter): ProductRepository
}