package com.example.project_shelf.framework.datastore

import androidx.datastore.core.DataStore
import com.example.project_self.proto.SavedInvoices
import com.example.project_shelf.adapter.dto.data_store.SavedInvoiceDto
import com.example.project_shelf.adapter.dto.data_store.toDto
import com.example.project_shelf.adapter.repository.SavedInvoicesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class SavedInvoicesDataStore @Inject constructor(
    private val dataStore: DataStore<SavedInvoices>
) : SavedInvoicesRepository {
    override fun get(): Flow<List<SavedInvoiceDto>> {
        return dataStore.data.map {
            it.invoicesList.map { it.toDto() }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SavedInvoicesDataStoreModule {
    @Singleton
    @Binds
    abstract fun bind(dataStore: SavedInvoicesDataStore): SavedInvoicesRepository
}