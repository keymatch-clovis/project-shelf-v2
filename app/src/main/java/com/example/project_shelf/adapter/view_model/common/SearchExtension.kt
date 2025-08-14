package com.example.project_shelf.adapter.view_model.common

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
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
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val result: Flow<PagingData<T>> = _query
        .onEach { _isLoading.update { true } }
        .debounce(500)
        .filter { it.isNotEmpty() }
        .flatMapLatest { onSearch(it) }
        .onEach { _isLoading.update { false } }
        .cachedIn(scope)

    fun updateQuery(value: String) {
        // NOTE: We are setting this as uppercase here just for UI purposes, as the business
        // converts almost everything to uppercase.
        _query.update { value.uppercase() }
    }
}