package com.example.project_shelf.app.service

import com.example.project_shelf.app.entity.Product
import kotlinx.coroutines.flow.Flow

interface ProductService {
    fun getProducts(): Flow<List<Product>>
}