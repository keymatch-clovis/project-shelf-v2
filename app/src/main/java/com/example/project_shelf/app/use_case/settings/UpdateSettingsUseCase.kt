package com.example.project_shelf.app.use_case.settings

import android.util.Log
import com.example.project_shelf.app.entity.Settings
import com.example.project_shelf.app.service.SettingsService
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(private val service: SettingsService) {
    suspend fun exec(shouldLoadDefaultData: Boolean) {
        Log.d("USE-CASE", "Updating settings with: $shouldLoadDefaultData")
        service.update(
            settings = Settings(
                shouldLoadDefaultData = shouldLoadDefaultData,
            )
        )
    }
}