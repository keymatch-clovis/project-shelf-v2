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
        defaultPrice: BigDecimal,
        stock: Int,
    ): Product {
        Log.d("SERVICE-IMPL", "Creating product with: $name, $defaultPrice, $stock")
        return database.withTransaction {
            // First, create the product.
            val productId = database.productDao().insert(
                ProductDto(
                    name = name,
                    defaultPrice = defaultPrice.toString(),
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
                defaultPrice = defaultPrice,
                stock = stock,
            )
        }
    }

    override suspend fun removeAll() {
        Log.d("PRODUCT-SERVICE", "Removing all products")
        database.productDao().delete()
    }

    override suspend fun remove(product: Product) {
        Log.d("PRODUCT-SERVICE", "Removing product: $product")
        database.productDao().delete(product.toDto())
    }

    override suspend fun update(product: Product) {
        Log.d("PRODUCT-SERVICE", "Updating product: $product")
        database.productDao().update(product.toDto())
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun bindRepository(impl: ProductServiceImpl): ProductService
}