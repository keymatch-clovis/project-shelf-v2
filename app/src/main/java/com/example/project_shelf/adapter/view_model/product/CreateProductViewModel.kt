package com.example.project_shelf.adapter.view_model.product

import android.icu.util.Currency
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.validator.validateBigDecimal
import com.example.project_shelf.adapter.view_model.common.validator.validateDouble
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.product.CreateProductUseCase
import com.example.project_shelf.app.use_case.product.CreateProductUseCaseInput
import dagger.hilt.android.lifecycle.HiltViewModel
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateProductViewModelState {
    data class InputState(
        val name: Input<String> = Input(),
        val price: Input<String> = Input(),
        val stock: Input<String> = Input(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val createProductUseCase: CreateProductUseCase,
) : ViewModel() {
    sealed interface Event {
        data class Created(val dto: ProductDto) : Event
    }

    private val isLoading = MutableStateFlow(false)

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    private val _inputState = MutableStateFlow(CreateProductViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    val isValid = _inputState
        .mapLatest {
            it.name.errors
                .isEmpty()
                .and(it.price.errors.isEmpty())
                .and(it.stock.errors.isEmpty())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        // Initialize all fields so the validations check all the default values.
        updateName()
        updatePrice()
        updateStock()

        viewModelScope.launch {
            // We need to check the name of the product is not repeated in the database.
            _inputState
                .distinctUntilChangedBy { _inputState.value.name.value }
                .onEach { isLoading.update { true } }
                .debounce(500)
                .mapLatest {
                    mutableListOf<ViewModelError>().apply {
                        _inputState.value.name.value?.let {
                            if (!productRepository.isProductNameUnique(it)) {
                                this.add(ViewModelError.PRODUCT_NAME_TAKEN)
                            }
                        }
                    }
                }
                .collectLatest { errors ->
                    _inputState.update { it.copy(name = it.name.copy(errors = it.name.errors + errors)) }
                }
                .also {
                    isLoading.update { false }
                }
        }
    }

    fun updateName(value: String? = null) = _inputState.update {
        it.copy(
            name = Input(
                value = value, errors = value.validateString(required = true)
            )
        )
    }

    fun updatePrice(value: String? = null) = _inputState.update {
        it.copy(
            price = Input(
                // We need to set a blank value to null, so the `String` to `BigDecimal`
                // transforming doesn't need another step.
                value = value?.ifBlank { null }, errors = value.validateBigDecimal()
            )
        )
    }

    fun updateStock(value: String? = null) = _inputState.update {
        it.copy(
            stock = Input(
                // We need to set a blank value to null, so the `String` to `Int` transforming
                // doesn't need another step.
                value = value?.ifBlank { null }, errors = value.validateInt()
            )
        )
    }

    fun create() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Creating product")
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        // NOTE:
        // > This is a risky transaction!
        // > Cephalon Sark
        val entity = createProductUseCase.exec(
            CreateProductUseCaseInput(
                name = _inputState.value.name.value!!,
                price = _inputState.value.price.value?.toLong(),
                stock = _inputState.value.stock.value?.toInt(),
            )
        )

        _eventFlow.emit(
            Event.Created(
                entity
                    // TODO: We can get the currency from a configuration option or something, but
                    //  for now we'll leave it hard coded.
                    .toDto(Currency.getInstance("COP"))
            )
        )
    }
}