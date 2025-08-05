package com.example.project_shelf.adapter.service_impl

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.project_shelf.proto.Settings as ProtoSettings
import com.example.project_shelf.app.entity.Settings
import com.example.project_shelf.app.service.SettingsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsServiceImpl @Inject constructor(
    private val datastore: DataStore<ProtoSettings>,
) : SettingsService {

    override fun get(): Flow<Settings> {
        Log.d("SERVICE-IMPL", "Getting settings")
        return datastore.data.map {
            Settings(
                shouldLoadDefaultData = it.shouldLoadDefaultData,
            )
        }
    }

    override suspend fun update(settings: Settings) {
        Log.d("SERVICE-IMPL", "Updating settings with: $settings")
        datastore.updateData {
            it.toBuilder()
                .setShouldLoadDefaultData(settings.shouldLoadDefaultData)
                .build()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsServiceModule {
    @Binds
    abstract fun bind(impl: SettingsServiceImpl): SettingsService
}