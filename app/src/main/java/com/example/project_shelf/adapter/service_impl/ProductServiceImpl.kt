package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
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

class ProductServiceImpl @Inject constructor(private val dao: ProductDao) : ProductService {
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE)
        ) { dao.select() }.flow.map {
            it.map { dto ->
                Product(
                    uuid = dto.uuid,
                    name = dto.name,
                    price = BigInteger(dto.price),
                    count = dto.count,
                )
            }
        }
    }

    override suspend fun createProduct(product: Product) {
        dao.insert(
            ProductDto(
                uuid = product.uuid,
                name = product.name,
                price = product.price.toString(),
                count = product.count,
            )
        )
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