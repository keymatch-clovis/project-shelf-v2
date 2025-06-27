package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {
    fun loadDefaultCities(stream: InputStream, onLoaded: () -> Unit) {
        viewModelScope.launch {
            Log.d("LOADING-VIEW-MODEL", "Loading default cities")
            if (!cityRepository.hasLoadedDefaultCities()) {
                cityRepository.loadDefaultCities(stream)
            }
            onLoaded()
        }
    }
}