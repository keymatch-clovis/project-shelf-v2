package com.example.project_shelf.adapter.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.framework.room.ShelfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun select(): PagingSource<Int, ProductDto>

    @Insert
    suspend fun insert(dto: ProductDto)
}

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {
    @Provides
    fun provideProductDao(@ApplicationContext context: Context): ProductDao {
        return ShelfDatabase.getInstance(context).database.productDao()
    }
}