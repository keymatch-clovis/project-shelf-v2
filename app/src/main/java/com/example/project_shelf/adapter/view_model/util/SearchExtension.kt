package com.example.project_shelf.adapter.view_model.util

import androidx.compose.runtime.snapshotFlow
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchExtension<T : Any>(
    private val scope: CoroutineScope,
    private val onSearch: (String) -> Flow<PagingData<T>>,
) {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val result: StateFlow<PagingData<T>> = snapshotFlow { _query.value }
        .debounce(300)
        .filter { it.isNotEmpty() }
        .flatMapLatest { onSearch(it) }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty<T>()
        )

    fun updateQuery(value: String) {
        // NOTE: We are setting this as uppercase here just for UI purposes, as the business
        // converts almost everything to uppercase.
        _query.update { value.uppercase() }
    }
}