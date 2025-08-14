package com.example.project_shelf.adapter.view_model.invoice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.dto.ui.toFilter
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.ObjectInput
import com.example.project_shelf.adapter.view_model.common.validator.validateObject
import com.example.project_shelf.app.use_case.customer.SearchCustomerUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = InvoiceDetailsFormViewModel.Factory::class)
class InvoiceDetailsFormViewModel @AssistedInject constructor(
    @Assisted val draft: InvoiceDraftDto?,
    private val searchCustomerUseCase: SearchCustomerUseCase,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(draft: InvoiceDraftDto?): InvoiceDetailsFormViewModel
    }

    data class State(
        val isLoading: Boolean = false,
        val number: String = "",
        val customer: ObjectInput<CustomerFilterDto> = ObjectInput(),
    )

    data class Callback(
        val onOpenCustomerSearch: () -> Unit,
        val onSetCustomer: (dto: CustomerFilterDto) -> Unit,
    )

    sealed interface Event {
        data class OnCustomerUpdated(val dto: CustomerFilterDto) : Event
    }

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Event related
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (draft != null) {
            if (draft.customerId != null) {
                _state.update { it.copy(isLoading = true) }

                viewModelScope.launch(context = Dispatchers.IO) {
                    Log.d("VIEW-MODEL", "Loading invoice details from draft: $draft")
                    searchCustomerUseCase.exec(draft.customerId)
                        ?.toFilter()
                        ?.let { customer ->
                            _state.update { it.copy(customer = ObjectInput(value = customer)) }
                        }
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    /// Update methods
    fun updateCustomer(dto: CustomerFilterDto) {
        _state.update {
            it.copy(
                customer = ObjectInput(
                    value = dto,
                    errors = dto.validateObject(required = true),
                )
            )
        }

        viewModelScope.launch(context = Dispatchers.Main) {
            _eventFlow.emit(Event.OnCustomerUpdated(dto))
        }
    }
}