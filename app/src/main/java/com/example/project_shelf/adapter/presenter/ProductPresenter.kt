package com.example.project_shelf.adapter.presenter

import android.icu.util.Currency
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.WorkManager
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.app.use_case.product.CreateProductUseCase
import com.example.project_shelf.app.use_case.product.DeleteProductUseCase
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import com.example.project_shelf.app.use_case.product.FindUseCase
import com.example.project_shelf.app.use_case.product.GetProductsUseCase
import com.example.project_shelf.app.use_case.product.MarkForDeletionUseCase
import com.example.project_shelf.app.use_case.product.RemoveAllProductsUseCase
import com.example.project_shelf.app.use_case.product.UnmarkForDeletionUseCase
import com.example.project_shelf.app.use_case.product.UpdateProductUseCase
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
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
    private val findProductsUseCase: FindUseCase,
    private val findProductUseCase: FindProductUseCase,
    private val markForDeletionUseCase: MarkForDeletionUseCase,
    private val unmarkForDeletionUseCase: UnmarkForDeletionUseCase,
) : ProductRepository {
    override fun find(): Flow<PagingData<ProductDto>> {
        return getProductsUseCase.exec().map {
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            it.map { product -> product.toDto(Currency.getInstance("COP")) }
        }
    }

    override fun search(name: String): Flow<PagingData<ProductFilterDto>> {
        Log.d("PRESENTER", "Getting products with: $name")
        return findProductsUseCase.exec(name).map {
            it.map { filter -> ProductFilterDto(name = filter.name) }
        }
    }

    override suspend fun getProduct(name: String): ProductDto? {
        Log.d("PRESENTER", "Getting product with: $name")
        return findProductUseCase.exec(name)
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            ?.toDto(Currency.getInstance("COP"))
    }

    override suspend fun updateProduct(
        id: Long,
        name: String,
        price: BigDecimal,
        stock: Int,
    ): ProductDto {
        Log.d("PRESENTER", "Creating product with: $name, $price, $stock")
        return updateProductUseCase.exec(id, name, price, stock)
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            .toDto(Currency.getInstance("COP"))
    }

    override suspend fun createProduct(name: String, price: BigDecimal, stock: Int): ProductDto {
        Log.d("PRESENTER", "Creating product with: $name, $price, $stock")

        return createProductUseCase.exec(
            name = name,
            price = price,
            stock = stock,
        )
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
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