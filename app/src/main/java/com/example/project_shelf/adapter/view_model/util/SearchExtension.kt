package com.example.project_shelf.adapter.view_model.util

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.repository.WithSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchExtension<T : Any>(
    private val scope: CoroutineScope,
    private val repository: WithSearch<T>,
) {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _result = MutableStateFlow<PagingData<T>>(PagingData.empty())
    val result = _result.asStateFlow()

    init {
        scope.launch {
            query
                .debounce(300)
                .filter { it.isNotEmpty() }
                .flatMapLatest {
                    repository.search(it)
                }.cachedIn(scope)
                .collectLatest {
                    _result.value = it
                }
        }
    }

    fun updateQuery(value: String) = _query.update { value }
}