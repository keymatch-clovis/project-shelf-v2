package com.example.project_shelf.adapter.view_model.customer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel()
class CustomerListViewModel @Inject constructor(
    repository: CustomerRepository,
) : ViewModel() {
    /// List related
    var customers: Flow<PagingData<CustomerDto>> = repository.find()
    var lazyListState: LazyListState by mutableStateOf(LazyListState(0, 0))

    /// Search related
    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    val search = SearchExtension(scope = viewModelScope, onSearch = {
        // TODO: Fix this
        repository.search(it)
    })

    fun closeSearchBar() = _showSearchBar.update { false }
    fun openSearchBar() {
        search.updateQuery("")
        _showSearchBar.update { true }
    }
}