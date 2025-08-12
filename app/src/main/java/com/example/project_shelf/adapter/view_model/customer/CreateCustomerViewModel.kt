package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.common.validator.validateObject
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.customer.CreateCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCustomerViewModelState {
    data class InputState(
        val name: Input<String> = Input(),
        val phone: Input<String> = Input(),
        val address: Input<String> = Input(),
        val businessName: Input<String> = Input(),
        val city: Input<CityFilterDto> = Input(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    cityRepository: CityRepository,
    private val customerRepository: CustomerRepository,
    private val createCustomerUseCase: CreateCustomerUseCase,
) : ViewModel() {
    sealed interface Event {
        data class Created(val dto: CustomerDto) : Event
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    private val _inputState = MutableStateFlow(CreateCustomerViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    val isValid = _inputState
        .mapLatest {
            it.name.errors
                .isEmpty()
                .and(it.phone.errors.isEmpty())
                .and(it.address.errors.isEmpty())
                .and(it.businessName.errors.isEmpty())
                .and(it.city.errors.isEmpty())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /// Related to city search and selection.
    private val _showCitySearchBar = MutableStateFlow(false)
    val showCitySearchBar = _showCitySearchBar.asStateFlow()

    val citySearch = SearchExtension<CityFilterDto>(
        scope = viewModelScope,
        // TODO: Fix this
        onSearch = { cityRepository.search(it) },
    )

    init {
        // Initialize all fields so the validations check all the default values.
        updateName()
        updatePhone()
        updateAddress()
        updateBusinessName()
        updateCity()
    }

    /// Update methods
    fun updateName(value: String? = null) = _inputState.update {
        it.copy(
            name = Input(
                value = value,
                errors = value.validateString(required = true),
            )
        )
    }

    fun updatePhone(value: String? = null) = _inputState.update {
        it.copy(
            phone = Input(
                value = value,
                errors = value.validateString(required = true),
            )
        )
    }

    fun updateAddress(value: String? = null) = _inputState.update {
        it.copy(
            address = Input(
                value = value,
                errors = value.validateString(required = false),
            )
        )
    }

    fun updateBusinessName(value: String? = null) = _inputState.update {
        it.copy(
            businessName = Input(
                value = value,
                errors = value.validateString(required = false),
            )
        )
    }

    fun updateCity(dto: CityFilterDto? = null) = _inputState.update {
        it.copy(
            city = Input(
                value = dto,
                errors = dto.validateObject(required = true),
            )
        )
    }

    fun closeCitySearchBar() = _showCitySearchBar.update { false }
    fun openCitySearchBar() {
        citySearch.updateQuery("")
        _showCitySearchBar.update { true }
    }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating customer")
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        // NOTE:
        // > This is a risky transaction!
        // > Cephalon Sark
        val entity = createCustomerUseCase.exec(
            name = _inputState.value.name.value!!,
            phone = _inputState.value.phone.value!!,
            address = _inputState.value.address.value!!,
            cityId = _inputState.value.city.value!!.id,
            businessName = _inputState.value.businessName.value,
        )

        _eventFlow.emit(Event.Created(entity.toDto()))
    }
}