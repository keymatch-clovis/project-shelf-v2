package com.example.project_shelf.lib.service

import com.example.project_shelf.lib.entity.Product
import kotlinx.coroutines.flow.Flow

interface ProductService {
    fun getProducts(): Flow<List<Product>>
}