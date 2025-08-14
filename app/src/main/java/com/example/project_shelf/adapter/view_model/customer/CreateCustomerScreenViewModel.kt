package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.ObjectInput
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.common.validator.validateObject
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.city.SearchCitiesUseCase
import com.example.project_shelf.app.use_case.customer.CreateCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateCustomerScreenViewModel @Inject constructor(
    private val createCustomerUseCase: CreateCustomerUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase,
) : ViewModel() {
    data class State(
        val isShowingCitySearch: Boolean = false,
        val name: Input = Input(),
        val phone: Input = Input(),
        val address: Input = Input(),
        val businessName: Input = Input(),
        val city: ObjectInput<CityFilterDto> = ObjectInput(),
    ) {
        /// Computed properties
        val isValid = name.errors.isEmpty()
            .and(phone.errors.isEmpty())
            .and(address.errors.isEmpty())
            .and(businessName.errors.isEmpty())
            .and(city.errors.isEmpty())
    }

    data class Callback(
        val onNameChange: (String) -> Unit,
        val onPhoneChange: (String) -> Unit,
        val onAddressChange: (String) -> Unit,
        val onBusinessNameChange: (String) -> Unit,
        val onCityChange: (CityFilterDto) -> Unit,
        val onCreateRequest: () -> Unit,
        val onDismissRequest: () -> Unit,
        val openCitySearch: () -> Unit,
        val closeCitySearch: () -> Unit,
    )

    sealed interface Event {
        data class Created(val dto: CustomerDto) : Event
    }

    /// Event related
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// Search related
    val citySearch = SearchExtension<CityFilterDto>(
        scope = viewModelScope,
        onSearch = {
            searchCitiesUseCase.exec(it)
                .map { it.map { it.toDto() } }
        },
    )

    init {
        // Initialize all fields so the validations check all the default
        // values.
        updateName("")
        updatePhone("")
        updateAddress("")
        updateBusinessName("")
        updateCity()
    }

    /// Update methods
    fun updateName(value: String) {
        _state.update {
            it.copy(
                name = Input(
                    value = value,
                    errors = value.validateString(required = true),
                )
            )
        }
    }

    fun updatePhone(value: String) {
        _state.update {
            it.copy(
                phone = Input(
                    value = value,
                    errors = value.validateString(required = true),
                )
            )
        }
    }

    fun updateAddress(value: String) {
        _state.update {
            it.copy(
                address = Input(
                    value = value,
                    errors = value.validateString(),
                )
            )
        }
    }

    fun updateBusinessName(value: String) {
        _state.update {
            it.copy(
                businessName = Input(
                    value = value,
                    errors = value.validateString(),
                )
            )
        }
    }

    fun updateCity(dto: CityFilterDto? = null) {
        _state.update {
            it.copy(
                city = ObjectInput(
                    value = dto,
                    errors = dto.validateObject(required = true),
                )
            )
        }
    }

    fun openCitySearchBar() {
        citySearch.updateQuery("")
        _state.update { it.copy(isShowingCitySearch = true) }
    }

    fun closeCitySearchBar() {
        _state.update { it.copy(isShowingCitySearch = false) }
    }

    fun create() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Creating customer")
            assert(_state.value.isValid)
            assert(_state.value.name.value.isNotBlank())

            // NOTE:
            // > This is a risky transaction!
            // > Cephalon Sark
            val entity = createCustomerUseCase.exec(
                name = _state.value.name.value,
                phone = _state.value.phone.value,
                cityId = _state.value.city.value!!.id,
                businessName = _state.value.businessName.value,
                address = _state.value.address.value,
            )

            _eventFlow.emit(Event.Created(entity.toDto()))
        }
    }
}