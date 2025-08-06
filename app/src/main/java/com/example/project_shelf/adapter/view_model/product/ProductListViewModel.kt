package com.example.project_shelf.adapter.view_model.product

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.SearchExtension
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase
import com.example.project_shelf.common.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel() {
    /// Event related
    sealed interface Event {
        data class RequestEdit(val dto: ProductDto) : Event
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    /// List related
    var products: Flow<PagingData<ProductDto>> = repository
        .get()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty(),
        )

    /// Related to product search.
    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    val search = SearchExtension(
        scope = viewModelScope,
        onSearch = {
            searchProductsUseCase
                .exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    fun closeSearchBar() {
        _showSearchBar.update { false }
    }

    fun openSearchBar() {
        search.updateQuery("")
        _showSearchBar.update { true }
    }

    /// Edit request related
    fun requestEdit(id: Id) = viewModelScope.launch {
        closeSearchBar()

        repository
            .find(id)
            .let { _eventFlow.emit(Event.RequestEdit(it)) }
    }
}