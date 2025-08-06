package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.app.use_case.debug.LoadProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val loadProductsUseCase: LoadProductsUseCase,
) : ViewModel() {
    @OptIn(ExperimentalUuidApi::class)
    fun loadTestProducts() = viewModelScope.launch {
        loadProductsUseCase.exec()
    }
}