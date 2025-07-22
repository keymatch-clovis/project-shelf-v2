package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.framework.datastore.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    sealed class Event {
        class Loaded : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    fun loadDefaultCities(stream: InputStream) {
        viewModelScope.launch {
            Log.d("LOADING-VIEW-MODEL", "Loading default cities")
            cityRepository.loadDefaultCities(stream)

            // Update data store to signal the first launch is done.
            dataStore.edit { it[Settings.IS_FIRST_TIME_OPEN_KEY] = false }

            _eventFlow.emit(Event.Loaded())
        }
    }
}