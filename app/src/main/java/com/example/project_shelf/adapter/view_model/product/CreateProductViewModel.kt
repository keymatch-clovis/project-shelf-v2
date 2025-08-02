package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.validator.BigDecimalValidator
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.validator.IntValidator
import com.example.project_shelf.adapter.view_model.util.validator.StringValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

sealed class CreateProductViewModelState {
    data class InputState(
        val name: Input<String, String> = Input("", StringValidator(required = true)),
        val price: Input<String, BigDecimal> = Input("", BigDecimalValidator()),
        val stock: Input<String, Int> = Input("", IntValidator()),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    sealed interface Event {
        data class Created(val dto: ProductDto) : Event
    }

    private val isLoading = MutableStateFlow(false)

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    val inputState = CreateProductViewModelState.InputState()

    val isValid = combineTransform<Boolean, Boolean>(
        inputState.name.errors.map { it.isEmpty() },
        inputState.price.errors.map { it.isEmpty() },
        inputState.stock.errors.map { it.isEmpty() },
        // The view model is valid when it is not loading something.
        isLoading.map { !it },
    ) {
        emit(it.all { it })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        // When the raw name value changes, we need to check if another product has this name, using
        // the clean name value.
        viewModelScope.launch {
            inputState.name.rawValue.onEach { isLoading.update { true } }.debounce(500).map {
                mutableListOf<ViewModelError>().apply {
                    inputState.name.cleanValue.value?.let {
                        if (!productRepository.isProductNameUnique(it)) {
                            this.add(ViewModelError.PRODUCT_NAME_TAKEN)
                        }
                    }
                }
            }.collectLatest {
                inputState.name.addErrors(*it.toTypedArray())
                isLoading.update { false }
            }
        }
    }

    fun updateName(value: String) = inputState.name.update(value)

    fun updatePrice(value: String) = inputState.price.update(value)

    fun updateStock(value: String) = inputState.stock.update(value)

    fun create() {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Creating product")
            // NOTE: We should only call this method when all input data has been validated.
            assert(isValid.value)

            val dto = productRepository.create(
                name = inputState.name.cleanValue.value!!,
                price = inputState.price.cleanValue.value ?: BigDecimal.ZERO,
                stock = inputState.stock.cleanValue.value ?: 0,
            )

            _eventFlow.emit(Event.Created(dto))
        }
    }
}