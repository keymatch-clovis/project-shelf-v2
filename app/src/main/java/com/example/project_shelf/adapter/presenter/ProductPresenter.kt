package com.example.project_shelf.adapter.presenter

import android.icu.util.Currency
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.worker.DeleteProductsMarkedForDeletionWorker
import com.example.project_shelf.adapter.worker.Tag
import com.example.project_shelf.app.use_case.product.CreateProductUseCase
import com.example.project_shelf.app.use_case.product.DeleteProductUseCase
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import com.example.project_shelf.app.use_case.product.FindProductsUseCase
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductPresenter @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val removeAllProductsUseCase: RemoveAllProductsUseCase,
    private val findProductsUseCase: FindProductsUseCase,
    private val findProductUseCase: FindProductUseCase,
    private val markForDeletionUseCase: MarkForDeletionUseCase,
    private val unmarkForDeletionUseCase: UnmarkForDeletionUseCase,
    private val workManager: WorkManager,
) : ProductRepository {
    override fun getProducts(): Flow<PagingData<ProductDto>> {
        return getProductsUseCase.exec().map {
            // TODO: We can get the currency from a configuration option or something, but for now we'll
            // leave it hard coded.
            it.map { product -> product.toDto(Currency.getInstance("COP")) }
        }
    }

    override fun getProducts(name: String): Flow<PagingData<ProductFilterDto>> {
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

    override suspend fun unmarkForDeletion(id: Long) {
        Log.d("PRESENTER", "Unmarking product for deletion: $id")
        unmarkForDeletionUseCase.exec(id)
    }

    override suspend fun markForDeletion(id: Long) {
        // NOTE: When we mark a product for deletion, we will execute the job for the deletion of
        // marked products for deletion. This will give us some time to ask the user if they want to
        // revert the mark applied to the product. This will not ensure the products are deleted
        // when the user asks to, so we have to make business rules checks for that.
        // This is the best way that I've thought about it, but I'm not completely sure this is ok.

        // So, first, mark the product for deletion.
        Log.d("PRESENTER", "Marking product for deletion: $id")
        markForDeletionUseCase.exec(id)

        // And then, enqueue the work to delete the products marked for deletion.
        val workRequest = OneTimeWorkRequestBuilder<DeleteProductsMarkedForDeletionWorker>()
            // NOTE: Ten seconds, because that's the time a Long Snackbar takes to be automatically
            // dismissed. This I have seen directly in the code, so the documentation for this is
            // a bit obscure.
            .addTag(Tag.DELETE_PRODUCTS_MARKED_FOR_DELETION.name)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        Log.d("PRESENTER", "Enqueuing delete products marked for deletion work")
        workManager.enqueueUniqueWork(
            Tag.DELETE_PRODUCTS_MARKED_FOR_DELETION.name,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    override suspend fun deleteAll() {
        Log.d("PRODUCT-PRESENTER", "Removing all products")
        removeAllProductsUseCase.exec()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductPresenterModule {
    @Binds
    abstract fun bindRepository(presenter: ProductPresenter): ProductRepository
}