package com.example.project_shelf.framework.room

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.project_shelf.framework.room.index.ProductIndex
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DATABASE_NAME = "shelf"
const val VERSION = 1

@Module
@InstallIn(SingletonComponent::class)
object ShelfDatabaseModule {
    @Singleton
    @Provides
    fun provideShelfDatabase(@ApplicationContext context: Context): SqliteDatabase {
        Log.d("DATABASE", "Creating database")
        return Room.databaseBuilder(
            context,
            SqliteDatabase::class.java,
            DATABASE_NAME,
        )
            .addCallback(ProductIndex())
            .build()
    }
}