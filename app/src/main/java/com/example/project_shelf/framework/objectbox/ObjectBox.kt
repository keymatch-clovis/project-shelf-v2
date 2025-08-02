package com.example.project_shelf.framework.objectbox

import android.content.Context
import com.example.project_shelf.adapter.dto.objectbox.MyObjectBox
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.objectbox.BoxStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObjectBoxModule {
    @Singleton
    @Provides
    fun provide(@ApplicationContext context: Context): BoxStore {
        // https://docs.objectbox.io/getting-started#create-a-store
        return MyObjectBox.builder().androidContext(context).build()
    }
}