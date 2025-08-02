package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.app.use_case.invoice.GetInvoiceDraftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel()
class InvoiceDraftListViewModel @Inject constructor(
    getInvoiceDraftsUseCase: GetInvoiceDraftsUseCase,
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    val isLoading = _loading.asStateFlow()

    private val _drafts = MutableStateFlow(emptyList<Date>())
    val drafts = _drafts.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Getting invoice drafts")
            _loading.update { true }
            getInvoiceDraftsUseCase.exec().map { it.date }.let { drafts ->
                _drafts.update { drafts }
            }
            _loading.update { false }
        }
    }
}