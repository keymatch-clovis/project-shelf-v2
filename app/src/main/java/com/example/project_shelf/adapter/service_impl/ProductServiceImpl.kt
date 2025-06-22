package com.example.project_shelf.adapter.service_impl

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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger
import javax.inject.Inject

class ProductServiceImpl @Inject constructor(private val dao: ProductDao) : ProductService {
    override fun getProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            )
        ) { dao.select() }.flow.map {
            it.map { dto ->
                Product(
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
                name = product.name,
                price = product.price.toString(),
                count = product.count,
            )
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun bindRepository(impl: ProductServiceImpl): ProductService
}