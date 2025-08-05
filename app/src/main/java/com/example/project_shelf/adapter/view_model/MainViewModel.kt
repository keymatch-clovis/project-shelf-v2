package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.app.use_case.city.LoadDefaultCitiesUseCase
import com.example.project_shelf.app.use_case.settings.GetSettingsUseCase
import com.example.project_shelf.app.use_case.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel()
class MainViewModel @Inject constructor(
    private val loadDefaultCitiesUseCase: LoadDefaultCitiesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : ViewModel() {
    sealed interface Event {
        class Loaded : Event
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    // We need to set this to true as the default value, because we expect the app to launch the
    // `loadDefaultData` method from somewhere.
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    suspend fun shouldLoadDefaultData(): Boolean {
        return getSettingsUseCase.exec().first().shouldLoadDefaultData
    }

    fun loadDefaultData(cityData: InputStream) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Loading default cities")
            loadDefaultCitiesUseCase.exec(cityData)

            // We need to update the settings store, so further app openings don't trigger the
            // loading of the default data.
            updateSettingsUseCase.exec(shouldLoadDefaultData = false)

            _eventFlow.emit(Event.Loaded())
        }
    }

    fun updateIsLoading(value: Boolean) = _isLoading.update { value }
}