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
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.StringValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateCustomerViewModelState {
    data class InputState(
        val name: Input<String> = Input("", StringValidator(required = true)),
        val phone: Input<String> = Input("", StringValidator()),
        val address: Input<String> = Input("", StringValidator()),
        val businessName: Input<String> = Input("", StringValidator()),
        // TODO:
        //  We have not created an input for this one, as the input changes a lot. We might want to
        //  create one for this later. But the replacement shouldn't be that hard. I hope :p
        val city: MutableStateFlow<CityDto?> = MutableStateFlow(null),
        val cityErrors: MutableStateFlow<List<ViewModelError>> = MutableStateFlow(emptyList()),
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

    val inputState = CreateCustomerViewModelState.InputState()

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
            _cityQuery.debounce(300).flatMapLatest {
                cityRepository.search(it.toString())
            }.cachedIn(viewModelScope).collectLatest {
                _citySearchResult.value = it
            }
        }

        viewModelScope.launch {
            combineTransform<List<ViewModelError>, Boolean>(
                inputState.name.errors,
                inputState.phone.errors,
                inputState.address.errors,
                inputState.cityErrors,
                inputState.businessName.errors,
            ) { it.all { it.isEmpty() } }.collectLatest {
                _isValid.update { it }
            }
        }
    }

    fun updateName(value: String) = inputState.name.update(value)
    fun updatePhone(value: String) = inputState.phone.update(value)
    fun updateAddress(value: String) = inputState.address.update(value)
    fun updateBusinessName(value: String) = inputState.businessName.update(value)

    fun updateCityQuery(value: String) = _cityQuery.update { value }
    fun updateCity(dto: CityDto?) = inputState.city.update { dto }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating customer")
        // NOTE: We should only call this method when all input data has been validated.
        assert(_isValid.value)

        customerRepository.create(
            name = inputState.name.cleanValue.value!!,
            phone = inputState.phone.cleanValue.value ?: "",
            address = inputState.address.cleanValue.value ?: "",
            businessName = inputState.businessName.cleanValue.value ?: "",
            cityId = inputState.city.value!!.id,
        )
    }
}