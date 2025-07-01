package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.entity.ProductSearch
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger
import javax.inject.Inject

// Recommended value for simple text data.
const val PAGE_SIZE = 100;

class ProductServiceImpl @Inject constructor(
    private val database: SqliteDatabase,
    private val dao: ProductDao,
) : ProductService {
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) { dao.select() }.flow.map {
            it.map { dto ->
                Product(
                    id = dto.rowId,
                    name = dto.name,
                    price = BigInteger(dto.price),
                    count = dto.count,
                )
            }
        }
    }

    override fun getProducts(name: String): Flow<PagingData<ProductSearch>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) {
            // Add a `*` at the end of the search to get a phrase query.
            // https://www.sqlite.org/fts3.html
            database.productFtsDao().match("$name*")
        }.flow.map {
            it.map { dto ->
                ProductSearch(
                    id = dto.productId,
                    name = dto.name,
                )
            }
        }
    }

    override suspend fun createProduct(
        name: String,
        price: BigInteger,
        count: Int,
    ) {
        database.withTransaction {
            // First, create the product.
            val productId = dao.insert(
                ProductDto(
                    name = name,
                    price = price.toString(),
                    count = count,
                )
            )

            // Then, store the FTS value.
            database.productFtsDao().insert(
                ProductFtsDto(
                    productId = productId,
                    name = name,
                )
            )
        }
    }

    override suspend fun removeAll() {
        Log.d("PRODUCT-SERVICE", "Removing all products")
        dao.delete()
    }

    override suspend fun remove(product: Product) {
        TODO("Not yet implemented")
    }

    override suspend fun update(product: Product) {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun bindRepository(impl: ProductServiceImpl): ProductService
}