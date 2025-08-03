package com.example.project_shelf.adapter.view_model.invoice

import androidx.lifecycle.ViewModel
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel()
class InvoiceDraftViewModel @Inject constructor() : ViewModel() {
    private val _currentDraft = MutableStateFlow<InvoiceDraftDto?>(null)
    val currentDraft = _currentDraft.asStateFlow()

    fun updateDraft(dto: InvoiceDraftDto) = _currentDraft.update { dto }
}