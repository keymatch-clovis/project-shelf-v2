package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.app.use_case.invoice.DeleteInvoiceDraftsUseCase
import com.example.project_shelf.app.use_case.invoice.GetInvoiceDraftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class InvoiceDraftListViewModel @Inject constructor(
    private val getInvoiceDraftsUseCase: GetInvoiceDraftsUseCase,
    private val deleteInvoiceDraftsUseCase: DeleteInvoiceDraftsUseCase,
) : ViewModel() {
    data class State(
        val isLoading: Boolean = true,
        val isEditing: Boolean = false,
        val drafts: List<InvoiceDraftDto> = emptyList(),
        val checkedItems: Set<Long> = emptySet(),
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(isLoading = true) }
        getDrafts()
        _state.update { it.copy(isLoading = false) }
    }

    fun getDrafts() {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Getting invoice drafts")
            getInvoiceDraftsUseCase.exec().map { it.toDto() }.let { drafts ->
                _state.update { it.copy(drafts = drafts) }
            }
        }
    }

    fun checkItem(id: Long) {
        _state.update { it.copy(checkedItems = it.checkedItems + id) }
    }

    fun uncheckItem(id: Long) {
        _state.update { it.copy(checkedItems = it.checkedItems - id) }

        // If we don't have more checked items, I feel it is good UX to just exit the editing mode.
        // The WhatsApp app does this.
        if (_state.value.checkedItems.isEmpty()) {
            _state.update { it.copy(isEditing = false) }
        }
    }

    fun checkAllItems() {
        _state.update { it.copy(checkedItems = it.drafts.map { it.id }.toSet()) }
    }

    fun clearCheckedItems() {
        _state.update { it.copy(checkedItems = emptySet()) }
    }

    fun deleteMarkedItems() {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Deleting marked items")
            _state.update { it.copy(isLoading = true) }
            deleteInvoiceDraftsUseCase.exec(*_state.value.checkedItems.toLongArray())

            exitEdition()
            clearCheckedItems()
            getDrafts()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun enterEdition() {
        _state.update { it.copy(isEditing = true) }
    }

    fun exitEdition() {
        _state.update { it.copy(isEditing = false) }
    }
}