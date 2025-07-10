package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto
import com.example.project_shelf.adapter.dto.room.toDto
import com.example.project_shelf.adapter.dto.room.toEntity
import com.example.project_shelf.adapter.dto.room.toProductFilter
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductFilter
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

// Recommended value for simple text data.
const val PAGE_SIZE = 100;

class ProductServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
) : ProductService {
    override fun find(): Flow<PagingData<Product>> {
        Log.d("SERVICE-IMPL", "Finding products")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) { database.productDao().select() }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override fun search(value: String): Flow<PagingData<ProductFilter>> {
        Log.d("SERVICE-IMPL", "Searching products with: $value")
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.productFtsDao().match("$value*")
        }.flow.map {
            it.map { dto -> dto.toProductFilter() }
        }
    }

    override suspend fun create(
        name: String,
        price: BigDecimal,
        stock: Int,
    ): Product {
        Log.d("SERVICE-IMPL", "Creating product with: $name, $price, $stock")
        return database.withTransaction {
            // First, create the product.
            val productId = database.productDao().insert(
                ProductDto(
                    name = name,
                    defaultPrice = price.toString(),
                    stock = stock,
                )
            )

            // Then, store the FTS value.
            database.productFtsDao().insert(
                ProductFtsDto(
                    productId = productId,
                    name = name,
                )
            )

            Product(
                id = productId,
                name = name,
                defaultPrice = price,
                stock = stock,
            )
        }
    }

    override suspend fun update(
        id: Long,
        name: String,
        price: BigDecimal,
        stock: Int,
    ): Product {
        Log.d("SERVICE-IMPL", "Updating product with: $id, $name, $price, $stock")
        val dto = ProductDto(
            rowId = id,
            name = name,
            defaultPrice = price.toString(),
            stock = stock,
        )
        database.productDao().update(dto)
        return dto.toEntity()
    }

    override suspend fun delete() {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Deleting all products")
            database.productDao().delete()

            Log.d("SERVICE-IMPL", "Deleting all products FTS")
            database.productFtsDao().delete()
        }
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Product[$id]: Deleting product")
            database.productDao().delete(id)

            Log.d("SERVICE-IMPL", "Product[$id]: Deleting product FTS")
            database.productFtsDao().delete(id)
        }
    }

    override suspend fun deletePendingForDeletion() {
        Log.d("SERVICE-IMPL", "Deleting products pending for deletion.")
        // As we need to remove more than just the entity, we have to first select the items.
        // NOTE:
        //  This might not be the best way of doing this, but we don't expect this to have more than
        //  10 or 100 items at a time.
        database.productDao().selectPendingForDeletion().forEach {
            delete(it.rowId)
        }
    }

    override suspend fun setPendingForDeletion(id: Long, until: Long) {
        Log.d("SERVICE-IMPL", "Product[$id]: Setting product pending for deletion")
        database.productDao().setPendingForDeletion(id, until)
    }

    override suspend fun unsetPendingForDeletion(id: Long) {
        Log.d("SERVICE-IMPL", "Product[$id]: Unsetting product pending for deletion")
        database.productDao().unsetPendingForDeletion(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductServiceModule {
    @Binds
    abstract fun bind(impl: ProductServiceImpl): ProductService
}