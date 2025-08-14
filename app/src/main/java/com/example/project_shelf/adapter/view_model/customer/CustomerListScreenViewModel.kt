package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.app.use_case.customer.SearchCustomersUseCase
import com.example.project_shelf.app.use_case.customer.SetCustomerPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.customer.UnsetCustomerPendingForDeletionUseCase
import com.example.project_shelf.common.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class CustomerListScreenViewModel @Inject constructor(
    private val setCustomerPendingForDeletionUseCase: SetCustomerPendingForDeletionUseCase,
    private val unsetCustomerPendingForDeletionUseCase: UnsetCustomerPendingForDeletionUseCase,
    private val searchCustomersUseCase: SearchCustomersUseCase,
) : ViewModel() {
    data class State(
        val isShowingSearch: Boolean = false,
        val customersMarkedForDeletion: List<CustomerDto> = emptyList(),
    )

    data class Callback(
        val onOpenSearch: () -> Unit,
        val onCloseSearch: () -> Unit,
        val onRequestCreateCustomer: () -> Unit,
        val onRequestEditCustomer: (customerId: Id) -> Unit,
        val onSetCustomerPendingForDeletion: (dto: CustomerDto) -> Unit,
        val onUnsetCustomerPendingForDeletion: (dto: CustomerDto) -> Unit,
        val onDismissCustomerMarkedForDeletion: (dto: CustomerDto) -> Unit,
    )

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Search related
    val search = SearchExtension<CustomerFilterDto>(
        scope = viewModelScope,
        onSearch = {
            searchCustomersUseCase.exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    fun setCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Setting customer pending for deletion: $dto")
            setCustomerPendingForDeletionUseCase.exec(dto.id)

            _state.update { it.copy(customersMarkedForDeletion = it.customersMarkedForDeletion + dto) }
        }
    }

    fun unsetCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Unsetting customer pending for deletion: $dto")
            unsetCustomerPendingForDeletionUseCase.exec(dto.id)
            dismissCustomerMarkedForDeletion(dto)
        }
    }

    fun onOpenSearch() {
        _state.update { it.copy(isShowingSearch = true) }
    }

    fun onCloseSearch() {
        _state.update { it.copy(isShowingSearch = false) }
    }

    /**
     * Not to confuse with the removal of customers marked for deletion use case---This is just to
     * remove the customer marked for deletion from the state's list. That's why the verb selection.
     * The actual deletion of the entities marked for deletion is done in the application layer. The
     * view models don't trigger this action directly.
     */
    fun dismissCustomerMarkedForDeletion(dto: CustomerDto) {
        _state.update { it.copy(customersMarkedForDeletion = it.customersMarkedForDeletion - dto) }
    }
}
