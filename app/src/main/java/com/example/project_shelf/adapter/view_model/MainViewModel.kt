package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel()
class MainViewModel @Inject constructor() : ViewModel() {
    private val _isAppReady = MutableStateFlow(false)
    val isAppReady = _isAppReady.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun isFirstAppLaunch(): Boolean {
        return false;
    }

    fun setAppReady() = _isAppReady.update { true }
}