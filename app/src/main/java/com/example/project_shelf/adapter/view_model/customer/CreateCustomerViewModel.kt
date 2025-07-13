package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.validateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCustomerViewModelState {
    data class InputState(
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val businessName: String = "",
    )

    data class ValidationState(
        val nameErrors: List<ViewModelError> = emptyList(),
        val phoneErrors: List<ViewModelError> = emptyList(),
        val addressErrors: List<ViewModelError> = emptyList(),
        val businessNameErrors: List<ViewModelError> = emptyList(),
    )
}

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {
    sealed class Event {
        class Created : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    private val _inputState = MutableStateFlow(CreateCustomerViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    private val _validationState = MutableStateFlow(CreateCustomerViewModelState.ValidationState())
    val validationState = _validationState.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()

    init {
        viewModelScope.launch {
            // When any of the UI inputs change, do the default checks.
            _inputState.collect { state ->
                _validationState.update {
                    it.copy(
                        nameErrors = state.name.validateString(required = true),
                        phoneErrors = state.phone.validateString(required = true),
                        addressErrors = state.address.validateString(required = true),
                        businessNameErrors = state.businessName.validateString(),
                    )
                }

                // Also update the view model valid state.
                _isValid.update {
                    listOf(
                        _validationState.value.nameErrors,
                        _validationState.value.phoneErrors,
                        _validationState.value.addressErrors,
                        _validationState.value.businessNameErrors,
                    ).all { it.isEmpty() }
                }
            }
        }
    }

    fun updateName(value: String) = _inputState.update { it.copy(name = value) }
    fun updatePhone(value: String) = _inputState.update { it.copy(phone = value) }
    fun updateAddress(value: String) = _inputState.update { it.copy(address = value) }
    fun updateBusinessName(value: String) = _inputState.update { it.copy(businessName = value) }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating customer")
        assert(_isValid.value)

        customerRepository.create(
            name = _inputState.value.name.trim(),
            phone = _inputState.value.phone.trim(),
            address = _inputState.value.address.trim(),
            cityId = TODO(),
            businessName = TODO(),
        )
    }
}