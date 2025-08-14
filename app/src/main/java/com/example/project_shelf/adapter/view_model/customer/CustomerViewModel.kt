package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.app.use_case.customer.SetCustomerPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.customer.UnsetCustomerPendingForDeletionUseCase
import com.example.project_shelf.common.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CustomerViewModelState {
    data class State(
        val selectedCustomerId: Id? = null,
        val customersMarkedForDeletion: List<CustomerDto> = emptyList(),
    )
}

@HiltViewModel()
class CustomerViewModel @Inject constructor(
    private val setCustomerPendingForDeletionUseCase: SetCustomerPendingForDeletionUseCase,
    private val unsetCustomerPendingForDeletionUseCase: UnsetCustomerPendingForDeletionUseCase,
) : ViewModel() {
    /// State related
    private val _state = MutableStateFlow(CustomerViewModelState.State())
    val state = _state.asStateFlow()

    fun setSelectedCustomerId(id: Id?) {
        _state.update { it.copy(selectedCustomerId = id) }
    }

    fun getSelectedCustomerId(): Id {
        return _state.value.selectedCustomerId!!.also {
            _state.update { it.copy(selectedCustomerId = null) }
        }
    }

    fun setCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Setting customer pending for deletion: $dto")
            setCustomerPendingForDeletionUseCase.exec(dto.id)
            _state.update { it.copy(customersMarkedForDeletion = it.customersMarkedForDeletion + dto) }
        }
    }

    fun unsetCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Unsetting customer pending for deletion: $dto")
            unsetCustomerPendingForDeletionUseCase.exec(dto.id)
            removeCustomerFromMarkedForDeletion(dto)
        }
    }

    fun removeCustomerFromMarkedForDeletion(dto: CustomerDto) {
        _state.update { it.copy(customersMarkedForDeletion = it.customersMarkedForDeletion - dto) }
    }
}
