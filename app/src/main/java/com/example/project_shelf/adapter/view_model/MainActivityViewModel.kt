package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.framework.ui.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: Destination = Destination.LOADING,
) : Serializable

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    fun checkHasLoadedDefaultData() {
        viewModelScope.launch {
            if (cityRepository.hasLoadedDefaultCities()) {
                _uiState.update { it.copy(startDestination = Destination.MAIN) }
            }
        }
    }
}