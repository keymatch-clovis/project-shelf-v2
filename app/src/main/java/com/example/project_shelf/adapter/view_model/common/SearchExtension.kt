package com.example.project_shelf.adapter.view_model.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.util.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchExtension<T : Any>(
    scope: CoroutineScope,
    onSearch: (String) -> Flow<PagingData<T>>,
) {
    data class State(
        val isSearchOpen: Boolean = false,
        val isLoading: Boolean = false,
        val query: String = "",
    )

    data class Callback(
        val onOpenSearch: () -> Unit,
        val onCloseSearch: () -> Unit,
        val onUpdateQuery: (String) -> Unit,
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    val result: Flow<PagingData<T>> = _state.distinctUntilChangedBy { it.query }
        .filter { it.query.isNotBlank() }
        .onEach { _state.update { it.copy(isLoading = true) } }
        .debounce(500)
        .flatMapLatest { onSearch(it.query) }
        .onEach { _state.update { it.copy(isLoading = false) } }
        .cachedIn(scope)

    /// Update methods
    fun openSearch() {
        _state.update { it.copy(isSearchOpen = true) }
    }

    fun closeSearch() {
        _state.update { it.copy(isSearchOpen = false, query = "") }
    }

    fun updateQuery(value: String) {
        // NOTE: We are setting this as uppercase here just for UI purposes, as the business
        // converts almost everything to uppercase.
        _state.update { it.copy(query = value.uppercase()) }
    }
}

fun <T : Any> ViewModel.addSearchExtension(
    onSearch: (String) -> Flow<PagingData<T>>,
): SearchExtension<T> {
    return SearchExtension(scope = viewModelScope, onSearch = onSearch)
}