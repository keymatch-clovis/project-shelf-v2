package com.example.project_shelf.adapter.view_model.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.util.BigDecimalValidator
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.adapter.view_model.util.IntValidator
import com.example.project_shelf.adapter.view_model.util.StringValidator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class EditProductViewModelState {
    data class UiState(
        val showConfirmDeletionDialog: Boolean = false,
    )

    data class InputState(
        val name: Input<String>,
        val price: Input<BigDecimal>,
        val stock: Input<Int>,
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val product: ProductDto,
    private val productRepository: ProductRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(product: ProductDto): EditProductViewModel
    }

    sealed class Event {
        class ProductUpdated : Event()
        class ProductMarkedForDeletion : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    private val _uiState = MutableStateFlow(EditProductViewModelState.UiState())
    val uiState = _uiState.asStateFlow()

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()

    val inputState = EditProductViewModelState.InputState(
        name = Input(product.name, StringValidator(required = true)),
        price = Input(product.price, BigDecimalValidator()),
        stock = Input(product.stock, IntValidator()),
    )

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

        // When the validation state changes, check if the view model is valid.
        viewModelScope.launch {
            combineTransform<List<ViewModelError>, Boolean>(
                inputState.name.errors,
                inputState.price.errors,
                inputState.stock.errors,
            ) { it.all { it.isEmpty() } }
        }
    }

    fun updateName(value: String) {
        inputState.name.update(value)
    }

    fun updatePrice(value: String) {
        inputState.price.update(value)
    }

    fun updateStock(value: String) {
        inputState.stock.update(value)
    }

    fun openConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = true) }
    }

    fun closeConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = false) }
    }

    fun edit() {
        // NOTE: We should only call this method when all input data has been validated.
        assert(_isValid.value)

        viewModelScope.launch {
            productRepository.update(
                id = product.id,
                name = inputState.name.cleanValue.value!!,
                price = inputState.price.cleanValue.value ?: BigDecimal.ZERO,
                stock = inputState.stock.cleanValue.value ?: 0,
            )
            _eventFlow.emit(Event.ProductUpdated())
        }
    }
}