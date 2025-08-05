package com.example.project_shelf.app.service

import com.example.project_shelf.app.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsService {
    fun get(): Flow<Settings>
    suspend fun update(settings: Settings)
}