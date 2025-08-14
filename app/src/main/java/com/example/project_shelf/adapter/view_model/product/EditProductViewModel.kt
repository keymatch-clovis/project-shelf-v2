package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.ProductRepository
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.common.validator.validateBigDecimal
import com.example.project_shelf.adapter.view_model.common.validator.validateInt
import com.example.project_shelf.adapter.view_model.common.validator.validateLong
import com.example.project_shelf.adapter.view_model.common.validator.validateString
import com.example.project_shelf.app.use_case.product.FindProductUseCase
import com.example.project_shelf.app.use_case.product.IsProductNameUniqueUseCase
import com.example.project_shelf.app.use_case.product.UpdateProductUseCase
import com.example.project_shelf.common.Id
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface EditProductViewModelState {
    data class State(
        val productDto: ProductDto? = null,
    )

    data class UiState(
        val isLoading: Boolean = false,
        val showConfirmDeletionDialog: Boolean = false,
    )

    data class InputState(
        val name: Input<String> = Input(),
        val price: Input<String> = Input(),
        val stock: Input<String> = Input(),
    )
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EditProductViewModel.Factory::class)
class EditProductViewModel @AssistedInject constructor(
    @Assisted val productId: Id,
    private val isProductNameUniqueUseCase: IsProductNameUniqueUseCase,
    private val findProductUseCase: FindProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Id): EditProductViewModel
    }

    /// State related
    private val state = MutableStateFlow(EditProductViewModelState.State())

    /// Event related
    sealed class Event {
        class ProductUpdated : Event()
        class ProductMarkedForDeletion : Event()
    }

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event> = _eventFlow

    /// UI state related
    private val _uiState = MutableStateFlow(EditProductViewModelState.UiState())
    val uiState = _uiState.asStateFlow()

    /// Input state related
    private val _inputState = MutableStateFlow(EditProductViewModelState.InputState())
    val inputState = _inputState.asStateFlow()

    val isValid = merge(_inputState, _uiState)
        .mapLatest {
            Log.d("test", _inputState.value.name.toString())
            Log.d("test", _inputState.value.price.toString())
            Log.d("test", _inputState.value.stock.toString())
            Log.d("test", _uiState.value.isLoading.toString())
            _inputState.value.name.errors
                .isEmpty()
                .and(_inputState.value.price.errors.isEmpty())
                .and(_inputState.value.stock.errors.isEmpty())
                .and(_inputState.value.name.value != state.value.productDto?.name)
                .and(!_uiState.value.isLoading)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        // Search for the product in the database, so we can get the information to edit it.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            findProductUseCase
                .exec(productId)
                .let { entity ->
                    state.update { it.copy(productDto = entity.toDto()) }
                    _inputState.update {
                        it.copy(
                            name = Input(value = entity.name),
                            price = Input(value = entity.defaultPrice.amount.toString()),
                            stock = Input(value = entity.stock.toString()),
                        )
                    }
                }

            _uiState.update { it.copy(isLoading = false) }
        }

        // We need to check the name of the product is not repeated in the database.
        viewModelScope.launch {
            _inputState
                .filter { _inputState.value.name.value != state.value.productDto?.name }
                .distinctUntilChangedBy { _inputState.value.name.value }
                .onEach { _uiState.update { it.copy(isLoading = true) } }
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
                .onEach { _uiState.update { it.copy(isLoading = false) } }
                .collectLatest { errors ->
                    _inputState.update { it.copy(name = it.name.copy(errors = errors)) }
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
                value = value, errors = value.validateBigDecimal()
            )
        )
    }

    fun updateStock(value: String) = _inputState.update {
        it.copy(
            stock = Input(
                value = value, errors = value.validateInt()
            )
        )
    }

    fun openConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = true) }
    }

    fun closeConfirmDeletionDialog() {
        _uiState.update { it.copy(showConfirmDeletionDialog = false) }
    }

    fun edit() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Editing product")

            // NOTE:
            // > This is a risky transaction!
            // > Cephalon Sark
            assert(isValid.value)

            updateProductUseCase.exec(
                id = productId,
                name = _inputState.value.name.value!!,
                price = _inputState.value.price.value!!.toBigDecimal(),
                stock = _inputState.value.stock.value!!.toInt(),
            )

            _eventFlow.emit(Event.ProductUpdated())
        }
    }
}