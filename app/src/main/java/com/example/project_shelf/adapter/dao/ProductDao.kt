package com.example.project_shelf.adapter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.ProductDto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun select(): Flow<List<ProductDto>>

    @Insert
    suspend fun insert(dto: ProductDto)
}