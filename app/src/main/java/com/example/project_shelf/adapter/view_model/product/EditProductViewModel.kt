package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.adapter.view_model.common.validator.validateLong
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.product.UpdateProductUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

sealed class EditProductViewModelState {
    data class UiState(
        val isLoading: Boolean = false,
        val showConfirmDeletionDialog: Boolean = false,
    )

    data class InputState(
        val name: Input<String>,
        val price: Input<String>,
        val stock: Input<String>,
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val product: ProductDto,
    private val productRepository: ProductRepository,
    private val updateProductUseCase: UpdateProductUseCase,
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

    private val _inputState = MutableStateFlow(
        EditProductViewModelState.InputState(
            name = Input(value = product.name),
            price = Input(value = product.price),
            stock = Input(value = product.stock),
        )
    )
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
        viewModelScope.launch {
            // We need to check the name of the product is not repeated in the database.
            _inputState
                .distinctUntilChangedBy { _inputState.value.name.value }
                .onEach { _uiState.update { it.copy(isLoading = true) } }
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
                    _inputState.update { it.copy(name = it.name.copy(errors = errors)) }
                }
                .also {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun updateName(value: String) = _inputState.update {
        it.copy(
            name = Input(
                value = value, errors = value.validateString(required = true)
            )
        )
    }

    fun updatePrice(value: String) = _inputState.update {
        it.copy(
            price = Input(
                value = value, errors = value.validateLong(required = true)
            )
        )
    }

    fun updateStock(value: String) = _inputState.update {
        it.copy(
            stock = Input(
                value = value, errors = value.validateInt(required = true)
            )
        )
    }

    fun openConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = true) }
    }

    fun closeConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = false) }
    }

    fun edit() = viewModelScope.launch {
        Log.d("VIEW-MODEL", "Editing product")

        // NOTE:
        // > This is a risky transaction!
        // > Cephalon Sark
        assert(isValid.value)

        updateProductUseCase.exec(
            id = product.id,
            name = _inputState.value.name.value!!,
            price = _inputState.value.price.value!!.toLong(),
            stock = _inputState.value.stock.value!!.toInt(),
        )

        _eventFlow.emit(Event.ProductUpdated())
    }
}