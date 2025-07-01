package com.example.project_shelf.adapter.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.adapter.dto.room.ProductFtsDto
import com.example.project_shelf.framework.room.SqliteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun select(): PagingSource<Int, ProductDto>

    @Query("DELETE FROM product")
    suspend fun delete()

    @Delete
    suspend fun delete(dto: ProductDto)

    @Insert
    suspend fun insert(dto: ProductDto): Long

    @Update
    suspend fun update(dto: ProductDto)
}

@Dao
interface ProductFtsDao {
    @Insert
    suspend fun insert(dto: ProductFtsDto)

    @Query("SELECT * FROM product_fts WHERE product_fts MATCH :value")
    fun match(value: String): PagingSource<Int, ProductFtsDto>
}

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {
    @Provides
    fun provideProductDao(database: SqliteDatabase): ProductDao {
        return database.productDao()
    }
}