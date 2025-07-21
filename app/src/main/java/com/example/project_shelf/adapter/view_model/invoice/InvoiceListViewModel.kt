package com.example.project_shelf.adapter.view_model.invoice

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.dto.ui.InvoiceFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceWithCustomerDto
import com.example.project_shelf.adapter.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel()
class InvoiceListViewModel @Inject constructor(
    repository: InvoiceRepository,
) : ViewModel() {
    var invoices: Flow<PagingData<InvoiceWithCustomerDto>> = repository.get()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))

    /// Related to invoice search.
    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _searchResult = MutableStateFlow<PagingData<InvoiceFilterDto>>(PagingData.empty())
    val searchResult = _searchResult.asStateFlow()

    fun openSearchBar() {
        _query.update { "" }
        _showSearchBar.update { true }
    }

    fun closeSearchBar() {
        _query.update { "" }
        _showSearchBar.update { false }
    }

    fun updateQuery(value: String) = _query.update { value }

    init {
        // Start listening for the query changes.
        viewModelScope.launch {
            _query.debounce(300).flatMapLatest {
                repository.search(it.toString())
            }.cachedIn(viewModelScope).collectLatest {
                _searchResult.value = it
            }
        }
    }
}