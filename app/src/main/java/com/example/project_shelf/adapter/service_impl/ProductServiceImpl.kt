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
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) { database.productDao().select() }.flow.map {
            it.map { dto -> dto.toEntity() }
        }
    }

    override fun getProducts(name: String): Flow<PagingData<ProductFilter>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.productFtsDao().match("$name*")
        }.flow.map {
            it.map { dto -> dto.toProductFilter() }
        }
    }

    override suspend fun getProduct(name: String): Product? {
        Log.d("SERVICE-IMPL", "Getting product with: $name")
        return database.productDao().select(name)?.toEntity()
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

    override suspend fun deleteAll() {
        Log.d("SERVICE-IMPL", "Deleting all products")
        database.productDao().delete()
    }

    override suspend fun delete(id: Long) {
        database.withTransaction {
            Log.d("SERVICE-IMPL", "Deleting product: $id")
            database.productDao().delete(id)

            Log.d("SERVICE-IMPL", "Deleting product FTS: $id")
            database.productFtsDao().delete(id)
        }
    }

    override suspend fun deleteMarkedForDeletion() {
        Log.d("SERVICE-IMPL", "Deleting products marked for deletion.")
        database.productDao().deleteMarkedForDeletion()
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

    override suspend fun markForDeletion(id: Long) {
        Log.d("SERVICE-IMPL", "Marking product for deletion: $id")
        database.productDao().markForDeletion(id)
    }

    override suspend fun unmarkForDeletion(id: Long) {
        Log.d("SERVICE-IMPL", "Unmarking product for deletion: $id")
        database.productDao().unmarkForDeletion(id)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductServiceModule {
    @Binds
    abstract fun bind(impl: ProductServiceImpl): ProductService
}