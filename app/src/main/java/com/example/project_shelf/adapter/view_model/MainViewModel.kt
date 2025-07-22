package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.example.project_shelf.framework.datastore.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel()
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _isAppReady = MutableStateFlow(false)
    val isAppReady = _isAppReady.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun isFirstAppLaunch(): Boolean {
        return dataStore.data.mapLatest {
            // If the value is null or true, it means the app is marked as first time open.
            // FIXME:
            //  This is not a correct abstraction, so we should change it to something like is app
            //  marked for restore, or something like that. For us to be able to restore the app at
            //  any other point, not only at first time open.
            it[Settings.IS_FIRST_TIME_OPEN_KEY] != false
        }.first()
    }

    fun setAppReady() = _isAppReady.update { true }
}