package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductSearchUiState(
    var isSearchBarExpanded: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val repository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductSearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _result: MutableStateFlow<PagingData<ProductFilterDto>> =
        MutableStateFlow(PagingData.empty())
    val result = _result.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest {
                    repository.getProducts(it.toString())
                }
                .cachedIn(viewModelScope)
                .collectLatest {
                    _result.value = it
                }
        }
    }

    fun updateQuery(value: String) {
        _query.update { value }
    }

    fun updateIsSearchBarExpanded(value: Boolean) {
        // When the search bar is closed, remove the query data.
        if (!value) {
            updateQuery("")
        }

        _uiState.update { it.copy(isSearchBarExpanded = value) }
    }
}