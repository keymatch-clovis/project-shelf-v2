package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.CityDto
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.validateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCustomerViewModelState {
    data class InputState(
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val city: CityDto? = null,
        val businessName: String = "",
    )

    data class ValidationState(
        val nameErrors: List<ViewModelError> = emptyList(),
        val phoneErrors: List<ViewModelError> = emptyList(),
        val addressErrors: List<ViewModelError> = emptyList(),
        val businessNameErrors: List<ViewModelError> = emptyList(),
        val cityErrors: List<ViewModelError> = emptyList(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
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

    // Related to city search and selection.
    private val _citySearchResult: MutableStateFlow<PagingData<CityDto>> =
        MutableStateFlow(PagingData.empty())
    var citySearchResult = _citySearchResult.asStateFlow()

    private val _cityQuery = MutableStateFlow("")
    val cityQuery = _cityQuery.asStateFlow()

    init {
        // Start listening for the city query changes.
        viewModelScope.launch {
            _cityQuery.debounce(300).distinctUntilChanged().flatMapLatest {
                cityRepository.search(it.toString())
            }.cachedIn(viewModelScope).collectLatest {
                _citySearchResult.value = it
            }
        }

        // When any of the UI inputs change, do the default checks.
        viewModelScope.launch {
            _inputState.collect { state ->
                _validationState.update {
                    // Check that the city has been set.
                    val cityErrors = mutableListOf<ViewModelError>()
                    if (state.city == null) {
                        cityErrors.add(ViewModelError.BLANK_VALUE)
                    }

                    it.copy(
                        nameErrors = state.name.validateString(required = true),
                        phoneErrors = state.phone.validateString(required = true),
                        addressErrors = state.address.validateString(required = true),
                        businessNameErrors = state.businessName.validateString(),
                        cityErrors = cityErrors,
                    )
                }

                // Also update the view model valid state.
                _isValid.update {
                    listOf(
                        _validationState.value.nameErrors,
                        _validationState.value.phoneErrors,
                        _validationState.value.addressErrors,
                        _validationState.value.businessNameErrors,
                        _validationState.value.cityErrors,
                    ).all { it.isEmpty() }
                }
            }
        }
    }

    fun updateName(value: String) = _inputState.update { it.copy(name = value) }
    fun updatePhone(value: String) = _inputState.update { it.copy(phone = value) }
    fun updateAddress(value: String) = _inputState.update { it.copy(address = value) }
    fun updateBusinessName(value: String) = _inputState.update { it.copy(businessName = value) }

    fun updateCityQuery(value: String) = _cityQuery.update { value }
    fun updateCity(dto: CityDto?) = _inputState.update { it.copy(city = dto) }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating customer")
        assert(_isValid.value)

        customerRepository.create(
            name = _inputState.value.name.trim(),
            phone = _inputState.value.phone.trim(),
            address = _inputState.value.address.trim(),
            cityId = _inputState.value.city!!.id,
            businessName = _inputState.value.businessName,
        )
    }
}