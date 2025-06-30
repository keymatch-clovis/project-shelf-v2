package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.framework.datastore.Settings
import com.example.project_shelf.framework.ui.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

data class MainActivityUiState(
    val isReady: Boolean = false,
    val startDestination: Destination = Destination.MAIN,
) : Serializable

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("MAIN-ACTIVITY-VIEW-MODEL", "Loading data store")

            val isFirstLaunch = dataStore.data.map {
                it[Settings.IS_FIRST_TIME_OPEN_KEY] != false
            }.first()

            _uiState.update {
                it.copy(
                    isReady = true,
                    startDestination = if (isFirstLaunch) Destination.LOADING else Destination.MAIN
                )
            }
        }
    }
}