package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.adapter.repository.CustomerRepository
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.SearchExtension
import com.example.project_shelf.adapter.view_model.util.validator.ObjectValidator
import com.example.project_shelf.adapter.view_model.util.validator.StringValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCustomerViewModelState {
    data class InputState(
        val name: Input<String, String> = Input("", StringValidator(required = true)),
        val phone: Input<String, String> = Input("", StringValidator(required = true)),
        val address: Input<String, String> = Input("", StringValidator(required = true)),
        val businessName: Input<String, String> = Input("", StringValidator()),
        val city: Input<CityFilterDto, CityFilterDto> = Input(
            null, ObjectValidator(required = true)
        ),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    cityRepository: CityRepository,
    private val customerRepository: CustomerRepository,
) : ViewModel() {
    sealed interface Event {
        data class Created(val dto: CustomerDto) : Event
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    val inputState = CreateCustomerViewModelState.InputState()


    val isValid = combineTransform<Boolean, Boolean>(
        inputState.name.errors.map { it.isEmpty() },
        inputState.phone.errors.map { it.isEmpty() },
        inputState.address.errors.map { it.isEmpty() },
        inputState.businessName.errors.map { it.isEmpty() },
        inputState.city.errors.map { it.isEmpty() },
    ) { emit(it.all { it }) }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Related to city search and selection.
    private val _showCitySearchBar = MutableStateFlow(false)
    val showCitySearchBar = _showCitySearchBar.asStateFlow()
    val citySearch = SearchExtension<CityFilterDto>(
        scope = viewModelScope,
        repository = cityRepository,
    )

    fun updateName(value: String) = inputState.name.update(value)
    fun updatePhone(value: String) = inputState.phone.update(value)
    fun updateAddress(value: String) = inputState.address.update(value)
    fun updateBusinessName(value: String) = inputState.businessName.update(value)
    fun updateCity(dto: CityFilterDto?) = inputState.city.update(dto)

    fun closeCitySearchBar() = _showCitySearchBar.update { false }
    fun openCitySearchBar() {
        citySearch.updateQuery("")
        _showCitySearchBar.update { true }
    }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating customer")
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        val dto = customerRepository.create(
            name = inputState.name.cleanValue.value!!,
            phone = inputState.phone.cleanValue.value ?: "",
            address = inputState.address.cleanValue.value ?: "",
            businessName = inputState.businessName.cleanValue.value ?: "",
            cityId = inputState.city.cleanValue.value!!.id,
        )

        _eventFlow.emit(Event.Created(dto))
    }
}