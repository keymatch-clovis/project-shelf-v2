package com.example.project_shelf.adapter.presenter

import android.icu.util.Currency
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.app.use_case.product.CreateProductUseCase
import com.example.project_shelf.app.use_case.product.CreateProductUseCaseInput
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import com.example.project_shelf.app.use_case.product.GetProductsUseCase
import com.example.project_shelf.app.use_case.product.IsProductNameUniqueUseCase
import com.example.project_shelf.app.use_case.product.MarkForDeletionUseCase
import com.example.project_shelf.app.use_case.product.RemoveAllProductsUseCase
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase
import com.example.project_shelf.app.use_case.product.UnmarkForDeletionUseCase
import com.example.project_shelf.app.use_case.product.UpdateProductUseCase
import com.example.project_shelf.common.Id
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

class ProductPresenter @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val findProductUseCase: FindProductUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val markForDeletionUseCase: MarkForDeletionUseCase,
    private val unmarkForDeletionUseCase: UnmarkForDeletionUseCase,
    private val isProductNameUniqueUseCase: IsProductNameUniqueUseCase,
) : ProductRepository {
    override fun get(): Flow<PagingData<ProductDto>> {
        return getProductsUseCase
            .exec()
            .map {
                // TODO:
                //  We can get the currency from a configuration option or something, but for now we'll
                //  leave it hard coded.
                it.map { product -> product.toDto(Currency.getInstance("COP")) }
            }
    }

    override suspend fun find(id: Id): ProductDto {
        Log.d("PRESENTER", "Product[$id]: finding product with ID")
        return findProductUseCase
            .exec(id)
            // TODO:
            //  We can get the currency from a configuration option or something, but for now we'll
            //  leave it hard coded.
            .toDto(Currency.getInstance("COP"))
    }

    override fun search(value: String): Flow<PagingData<ProductFilterDto>> {
        Log.d("PRESENTER", "Searching products with: $value")
        return searchProductsUseCase
            .exec(value)
            .map {
                // TODO:
                //  We can get the currency from a configuration option or something, but for now we'll
                //  leave it hard coded.
                it.map { dto -> dto.toDto() }
            }
    }

    override suspend fun isProductNameUnique(name: String): Boolean {
        Log.d("PRESENTER", "Checking if product name: $name, is unique")
        return isProductNameUniqueUseCase.exec(name)
    }

    override suspend fun update(
        id: Long,
        name: String,
        price: BigDecimal,
        stock: Int,
    ): ProductDto {
        Log.d("PRESENTER", "Creating product with: $name, $price, $stock")
        return updateProductUseCase
            .exec(id, name, price, stock)
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            .toDto(Currency.getInstance("COP"))
    }

    override suspend fun create(name: String, price: BigDecimal, stock: Int): ProductDto {
        Log.d("PRESENTER", "Creating product with: $name, $price, $stock")

        return createProductUseCase
            .exec(
                CreateProductUseCaseInput(
                    name = name,
                    price = price,
                    stock = stock,
                )
            )
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            //  leave it hard coded.
            .toDto(Currency.getInstance("COP"))
    }

    override suspend fun setPendingForDeletion(id: Long) {
        Log.d("PRESENTER", "Product[$id]: Marking for deletion")
        markForDeletionUseCase.exec(id)
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        Log.d("PRESENTER", "Product[$id]: Unmarking product for deletion")
        unmarkForDeletionUseCase.exec(id)
    }

    override suspend fun deleteAll() {
        Log.d("CT-PRESENTER", "Removing all products")
        removeAllProductsUseCase.exec()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductPresenterModule {
    @Binds
    abstract fun bindRepository(presenter: ProductPresenter): ProductRepository
}