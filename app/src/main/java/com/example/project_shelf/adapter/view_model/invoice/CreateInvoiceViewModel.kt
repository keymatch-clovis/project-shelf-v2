package com.example.project_shelf.adapter.view_model.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.util.SearchExtension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel()
class CreateInvoiceViewModel @Inject constructor(
    customerRepository: CustomerRepository,
) : ViewModel() {
    sealed interface Event {
        object OpenSearchCustomer : Event
    }

    val eventFlow = MutableSharedFlow<Event>()

    /// Related to customer search.
    private val _showCustomerSearchBar = MutableStateFlow(false)
    val showCustomerSearchBar = _showCustomerSearchBar.asStateFlow()
    val search = SearchExtension<CustomerFilterDto>(
        scope = viewModelScope,
        repository = customerRepository,
    )

    init {
        viewModelScope.launch {
            eventFlow.collectLatest {
                when (it) {
                    Event.OpenSearchCustomer -> _showCustomerSearchBar.update { true }
                }
            }
        }
    }

    fun openCustomerSearchBar() = _showCustomerSearchBar.update { true }
    fun closeCustomerSearchBar() {
        search.updateQuery("")
        _showCustomerSearchBar.update { false }
    }
}