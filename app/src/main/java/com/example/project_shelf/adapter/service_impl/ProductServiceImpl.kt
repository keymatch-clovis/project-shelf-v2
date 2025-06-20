package com.example.project_shelf.adapter.service_impl

import com.example.project_shelf.adapter.dao.ProductDao
import com.example.project_shelf.app.entity.Product
import com.example.project_shelf.app.service.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigInteger

class ProductServiceImpl(private val dao: ProductDao) : ProductService {
    override fun getProducts(): Flow<List<Product>> {
        return dao.select().map {
            it.map { dto ->
                Product(
                    name = dto.name,
                    price = BigInteger(dto.price),
                    count = dto.count,
                )
            }
        }
    }
}