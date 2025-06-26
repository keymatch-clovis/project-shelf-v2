package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun removeAllData(onRemoved: suspend () -> Unit) {
        viewModelScope.launch {
            productRepository.removeAll()

            onRemoved()
        }
    }
}