package com.example.project_shelf.framework.datastore.serializer

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.example.project_self.proto.SavedInvoices
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

object SavedInvoicesSerializer : Serializer<SavedInvoices> {
    override val defaultValue: SavedInvoices = SavedInvoices.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SavedInvoices {
        try {
            return SavedInvoices.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: SavedInvoices, output: OutputStream) = t.writeTo(output)
}

@Module
@InstallIn(SingletonComponent::class)
object SavedInvoicesModule {
    @Singleton
    @Provides
    fun provide(@ApplicationContext context: Context): DataStore<SavedInvoices> {
        return DataStoreFactory.create(
            serializer = SavedInvoicesSerializer,
            produceFile = {
                File("${context.cacheDir.path}/saved_invoices.pb")
            },
            corruptionHandler = ReplaceFileCorruptionHandler { SavedInvoices.getDefaultInstance() })
    }
}