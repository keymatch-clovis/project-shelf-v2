package com.example.project_shelf.adapter.worker

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

enum class Tag {
    DELETE_PRODUCTS_MARKED_FOR_DELETION
}

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext context: Context) = WorkManager.getInstance(context)
}