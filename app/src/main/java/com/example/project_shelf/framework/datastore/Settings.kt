package com.example.project_shelf.framework.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.example.project_shelf.proto.Settings
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

class SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings =
        Settings.newBuilder().setShouldLoadDefaultData(true).build()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) = t.writeTo(output)

}

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Singleton
    @Provides
    fun provide(@ApplicationContext context: Context): DataStore<Settings> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer(),
            produceFile = {
                File("${context.cacheDir.path}/project_shelf.preferences")
            },
            corruptionHandler = ReplaceFileCorruptionHandler { Settings.getDefaultInstance() },
        )
    }
}