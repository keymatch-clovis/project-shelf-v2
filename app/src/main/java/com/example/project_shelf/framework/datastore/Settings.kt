package com.example.project_shelf.framework.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey

object Settings {
    val IS_FIRST_TIME_OPEN_KEY = booleanPreferencesKey("is_first_time_open")
}