package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.BigDecimalValidator
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.IntValidator
import com.example.project_shelf.adapter.view_model.util.StringValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal

sealed class CreateProductViewModelState {
    data class InputState(
        val name: Input<String> = Input("", StringValidator(required = true)),
        val price: Input<BigDecimal> = Input("", BigDecimalValidator()),
        val stock: Input<Int> = Input("", IntValidator()),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    sealed class Event {
        data class ProductCreated(val product: ProductDto) : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    val inputState = CreateProductViewModelState.InputState()

    val isValid = combineTransform<List<ViewModelError>, Boolean>(
        inputState.name.errors,
        inputState.price.errors,
        inputState.stock.errors,
    ) {
        emit(it.all { it.isEmpty() })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        // When the name changes, we need to check if another product has this name.
        viewModelScope.launch {
            inputState.name.cleanValue.debounce(300).mapLatest {
                mutableListOf<ViewModelError>().apply {
                    if (it != null && !productRepository.isProductNameUnique(it)) {
                        this.add(ViewModelError.PRODUCT_NAME_TAKEN)
                    }
                }
            }.collectLatest {
                inputState.name.addErrors(*it.toTypedArray())
            }
        }

        // Verify the input state is valid.
        viewModelScope.launch {
            isValid.collect {
                Log.d("test", it.toString())
            }
        }
    }

    fun updateName(value: String) = inputState.name.update(value)
    fun updatePrice(value: String) = inputState.price.update(value)
    fun updateStock(value: String) = inputState.stock.update(value)

    fun create() {
        Log.d("VIEW-MODEL", "Creating product")
        // NOTE: We should only call this method when all input data has been validated.
        assert(isValid.value)

        viewModelScope.launch {
            val product = productRepository.create(
                name = inputState.name.cleanValue.value!!,
                price = inputState.price.cleanValue.value ?: BigDecimal.ZERO,
                stock = inputState.stock.cleanValue.value ?: 0,
            )
            _eventFlow.emit(Event.ProductCreated(product))
        }
    }
}