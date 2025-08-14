package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductFilter
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.service.model.CreateProductInput
import com.example.project_shelf.app.service.model.UpdateProductInput
import com.example.project_shelf.common.Id
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.money.Money
import javax.inject.Inject

// Recommended value for simple text data.
const val PAGE_SIZE = 100;

class ProductServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : ProductService {
    override fun get(): Flow<PagingData<Product>> {
        Log.d("IMPL", "Finding products")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            database
                .productDao()
                .select()
        }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override suspend fun findByName(name: String): Product? {
        Log.d("IMPL", "Finding product with name: $name")
        return database
            .productDao()
            .selectByName(name)
            ?.toEntity()
    }

    override suspend fun findById(id: Id): Product {
        Log.d("IMPL", "Product[$id]: finding product with ID")
        return database
            .productDao()
            .select(id)
            .toEntity()
    }

    override fun search(value: String): Flow<PagingData<ProductFilter>> {
        Log.d("IMPL", "Searching products with: $value")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database
                .productFtsDao()
                .match("$value*")
        }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override suspend fun search(id: Id): Product? {
        Log.d("IMPL", "Searching product with ID: $id")
        return database
            .productDao()
            .search(id)
            ?.toEntity()
    }

    override suspend fun create(input: CreateProductInput): Product {
        Log.d("IMPL", "Creating product with: $input")
        return database.withTransaction {
            // First, create the product.
            val productId = database
                .productDao()
                .insert(
                    ProductDto(
                        name = input.name,
                        defaultPrice = input.defaultPrice,
                        stock = input.stock,
                    )
                )

            // Then, store the FTS value.
            database
                .productFtsDao()
                .insert(
                    ProductFtsDto(
                        productId = productId,
                        name = input.name,
                    )
                )

            Product(
                id = productId,
                name = input.name,
                defaultPrice = Money.ofMinor(currencyUnitFromDefaultLocale(), input.defaultPrice),
                stock = input.stock,
            )
        }
    }

    override suspend fun create(input: List<CreateProductInput>): List<Product> {
        Log.d("IMPL", "Creating products with: $input")
        return database.withTransaction {
            input.map {
                // First, create the product.
                val productId = database
                    .productDao()
                    .insert(
                        ProductDto(
                            name = it.name,
                            defaultPrice = it.defaultPrice,
                            stock = it.stock,
                        )
                    )

                // Then, store the FTS value.
                database
                    .productFtsDao()
                    .insert(
                        ProductFtsDto(
                            productId = productId,
                            name = it.name,
                        )
                    )

                Product(
                    id = productId,
                    name = it.name,
                    defaultPrice = Money.ofMinor(currencyUnitFromDefaultLocale(), it.defaultPrice),
                    stock = it.stock,
                )
            }
        }
    }

    override suspend fun update(input: UpdateProductInput): Product {
        Log.d("IMPL", "Updating product with: $input")
        val dto = ProductDto(
            rowId = input.id,
            name = input.name,
            defaultPrice = input.price,
            stock = input.stock,
        )
        database
            .productDao()
            .update(dto)
        return dto.toEntity()
    }

    override suspend fun delete() {
        database.withTransaction {
            Log.d("IMPL", "Deleting all products")
            database
                .productDao()
                .delete()

            Log.d("IMPL", "Deleting all products FTS")
            database
                .productFtsDao()
                .delete()
        }
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("IMPL", "Product[$id]: Deleting product")
            database
                .productDao()
                .delete(id)

            Log.d("IMPL", "Product[$id]: Deleting product FTS")
            database
                .productFtsDao()
                .delete(id)
        }
    }

    override suspend fun deletePendingForDeletion() {
        Log.d("IMPL", "Deleting products pending for deletion.")
        // As we need to remove more than just the entity, we have to first select the items.
        // NOTE:
        //  This might not be the best way of doing this, but we don't expect this to have more than
        //  10 or 100 items at a time.
        database
            .productDao()
            .selectPendingForDeletion()
            .forEach {
                delete(it.rowId)
            }
    }

    override suspend fun setPendingForDeletion(id: Long, until: Long) {
        Log.d("IMPL", "Product[$id]: Setting product pending for deletion")
        database
            .productDao()
            .setPendingForDeletion(id, until)
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        Log.d("IMPL", "Product[$id]: Unsetting product pending for deletion")
        database
            .productDao()
            .unsetPendingForDeletion(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductServiceModule {
    @Binds
    abstract fun bind(impl: ProductServiceImpl): ProductService
}