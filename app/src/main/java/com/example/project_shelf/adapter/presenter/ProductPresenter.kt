package com.example.project_shelf.adapter.presenter

import android.icu.util.Currency
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.room.toDto
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.app.use_case.CreateProductUseCase
import com.example.project_shelf.app.use_case.FindProductUseCase
import com.example.project_shelf.app.use_case.FindProductsUseCase
import com.example.project_shelf.app.use_case.GetProductsUseCase
import com.example.project_shelf.app.use_case.RemoveAllProductsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class ProductPresenter @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
    private val findProductsUseCase: FindProductsUseCase,
    private val findProductUseCase: FindProductUseCase,
) : ProductRepository {
    override fun getProducts(): Flow<PagingData<ProductDto>> {
        return getProductsUseCase.exec().map {
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            it.map { product -> product.toDto(Currency.getInstance("COP")) }
        }
    }

    override fun getProducts(name: String): Flow<PagingData<ProductFilterDto>> {
        Log.d("USE-CASE", "Getting products with: $name")
        return findProductsUseCase.exec(name).map {
            it.map { filter -> ProductFilterDto(name = filter.name) }
        }
    }

    override suspend fun getProduct(name: String): ProductDto? {
        Log.d("USE-CASE", "Getting product with: $name")
        // TODO: We can get the currency from a configuration option or something, but for now we'll
        // leave it hard coded.
        return findProductUseCase.exec(name)?.toDto(Currency.getInstance("COP"))
    }

    override suspend fun updateProduct(
        id: Long,
        name: String,
        price: BigDecimal,
        stock: Int
    ): ProductDto {
        TODO("Not yet implemented")
    }

    override suspend fun createProduct(name: String, price: BigDecimal, stock: Int): ProductDto {
        Log.d("PRODUCT-PRESENTER", "Creating product with: $name, $price, $stock")

        return createProductUseCase.exec(
            name = name,
            price = price,
            stock = stock,
        )
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            .toDto(Currency.getInstance("COP"))
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