package com.example.project_shelf.framework.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.project_self.proto.SavedInvoices
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

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

val Context.createInvoiceDataStore: DataStore<SavedInvoices> by dataStore(
    fileName = "savedInvoices.pb",
    serializer = SavedInvoicesSerializer,
)