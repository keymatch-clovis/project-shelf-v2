package com.example.project_shelf.adapter.view_model.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.InvoiceWithCustomerDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.app.use_case.invoice.GetInvoicesWithCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel()
class InvoiceListViewModel @Inject constructor(
    getInvoicesWithCustomersUseCase: GetInvoicesWithCustomersUseCase,
) : ViewModel() {
    data class State(
        val isShowingSearch: Boolean = false,
    )

    data class Callback(
        val onQueryUpdate: (String) -> Unit,
    )

    val invoices: Flow<PagingData<InvoiceWithCustomerDto>> =
        getInvoicesWithCustomersUseCase.exec().map { it.map { it.toDto() } }
            .cachedIn(viewModelScope)

    /// Search related
    val search = SearchExtension<Any>(
        scope = viewModelScope,
        onSearch = { TODO() },
    )

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Related to invoice search.
    fun openSearch() {
        _state.update { it.copy(isShowingSearch = true) }
    }

    fun closeSearchBar() {
        _state.update { it.copy(isShowingSearch = false) }
    }

    fun updateQuery(value: String) = search.updateQuery(value)
}