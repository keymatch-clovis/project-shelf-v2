package com.example.project_shelf.adapter.view_model.invoice

import androidx.lifecycle.ViewModel
import com.example.project_shelf.adapter.repository.SavedInvoicesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel()
class SavedInvoicesViewModel @Inject constructor(
    savedInvoicesRepository: SavedInvoicesRepository,
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
}