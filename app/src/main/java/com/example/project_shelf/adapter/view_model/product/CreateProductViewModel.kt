package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.validator.validateBigDecimal
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.product.CreateProductUseCase
import com.example.project_shelf.app.use_case.product.CreateProductUseCaseInput
import com.example.project_shelf.app.use_case.product.IsProductNameUniqueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateProductViewModelState {
    data class InputState(
        val name: Input = Input(),
        val price: Input = Input(),
        val stock: Input = Input(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val createProductUseCase: CreateProductUseCase,
    private val isProductNameUniqueUseCase: IsProductNameUniqueUseCase,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)

    /// Event related
    sealed interface Event {
        data class Created(val dto: ProductDto) : Event
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    /// Input state related
    private val _inputState = MutableStateFlow(CreateProductViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    val isValid = merge(_inputState, isLoading).mapLatest {
        _inputState.value.name.errors.isEmpty()
            .and(_inputState.value.price.errors.isEmpty())
            .and(_inputState.value.stock.errors.isEmpty())
            .and(!isLoading.value)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        // Initialize all fields so the validations check all the default values.
        updateName("")
        updatePrice("")
        updateStock("")

        // We need to check the name of the product is not repeated in the database.
        viewModelScope.launch {
            _inputState.distinctUntilChangedBy { _inputState.value.name.value }
                .onEach { isLoading.update { true } }
                .debounce(500)
                .mapLatest {
                    mutableListOf<ViewModelError>().apply {
                        _inputState.value.name.value?.let {
                            if (!isProductNameUniqueUseCase.exec(it)) {
                                this.add(ViewModelError.PRODUCT_NAME_TAKEN)
                            }
                        }
                    }
                }
                .onEach { isLoading.update { false } }
                .collectLatest { errors ->
                    _inputState.update { it.copy(name = it.name.copy(errors = it.name.errors + errors)) }
                }
        }
    }

    fun updateName(value: String) {
        _inputState.update {
            it.copy(name = Input(value = value, errors = value.validateString(required = true)))
        }
    }

    fun updatePrice(value: String) {
        _inputState.update {
            it.copy(price = Input(value = value, errors = value.validateBigDecimal()))
        }
    }

    fun updateStock(value: String) {
        _inputState.update {
            it.copy(stock = Input(value = value, errors = value.validateInt()))
        }
    }

    fun create() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Creating product")
            // NOTE: We should only call this method when all input data has been validated.
            assert(isValid.value)
            assert(_inputState.value.name.value.isNotBlank())

            // NOTE:
            // > This is a risky transaction!
            // > Cephalon Sark
            val entity = createProductUseCase.exec(
                CreateProductUseCaseInput(
                    name = _inputState.value.name.value,
                    price = _inputState.value.price.value.toBigDecimalOrNull(),
                    stock = _inputState.value.stock.value.toIntOrNull(),
                )
            )

            _eventFlow.emit(Event.Created(entity.toDto()))
        }
    }
}