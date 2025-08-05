package com.example.project_shelf.app.use_case.settings

import android.util.Log
import com.example.project_shelf.app.entity.Settings
import com.example.project_shelf.app.service.SettingsService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val service: SettingsService) {
    fun exec(): Flow<Settings> {
        Log.d("USE-CASE", "Getting settings")
        return service.get()
    }
}