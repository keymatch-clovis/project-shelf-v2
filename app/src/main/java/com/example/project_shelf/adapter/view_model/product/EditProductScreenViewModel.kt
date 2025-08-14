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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EditProductScreenViewModel.Factory::class)
class EditProductScreenViewModel @AssistedInject constructor(
    @Assisted val productId: Id,
    private val isProductNameUniqueUseCase: IsProductNameUniqueUseCase,
    private val findProductUseCase: FindProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(id: Id): EditProductScreenViewModel
    }

    data class State(
        val isLoading: Boolean = false,
        val productDto: ProductDto? = null,
        val name: Input = Input(),
        val defaultPrice: Input = Input(),
        val stock: Input = Input(),
    ) {
        /// Computed properties
        val isValid = name.errors.isEmpty()
            .and(defaultPrice.errors.isEmpty())
            .and(stock.errors.isEmpty())
            .and(name.value != productDto?.name)
            .and(!isLoading)
    }

    data class Callback(
        val onNavigateBack: () -> Unit,
        val onRequestDeleteProduct: () -> Unit,
        val onRequestEditProduct: () -> Unit,
        val onUpdateName: (value: String) -> Unit,
        val onUpdateDefaultPrice: (value: String) -> Unit,
        val onUpdateStock: (value: String) -> Unit,
    )

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        // Search for the product in the database, so we can get the information to edit it.
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch(context = Dispatchers.IO) {
            findProductUseCase.exec(productId)
                .let { entity ->
                    _state.update {
                        it.copy(
                            productDto = entity.toDto(),
                        )
                    }
                    updateName(entity.name)
                    updateDefaultPrice(entity.defaultPrice.amount.toString())
                    updateStock(entity.stock.toString())
                }

            _state.update { it.copy(isLoading = false) }
        }

        // We need to check the name of the product is not repeated in the database.
        viewModelScope.launch(context = Dispatchers.IO) {
            _state.distinctUntilChangedBy { it.name.value }
                .filter { it.name.value.isNotBlank() }
                .onEach { _state.update { it.copy(isLoading = true) } }
                .debounce(500)
                .mapLatest {
                    val errors = mutableListOf<ViewModelError>()
                    it.name.value.let {
                        if (!isProductNameUniqueUseCase.exec(it)) {
                            errors.add(ViewModelError.PRODUCT_NAME_TAKEN)
                        }
                    }

                    errors
                }
                .onEach { _state.update { it.copy(isLoading = false) } }
                .collectLatest { errors ->
                    _state.update { it.copy(name = it.name.copy(errors = errors)) }
                }
        }
    }

    fun updateName(value: String) {
        _state.update {
            it.copy(
                name = Input(
                    value = value, errors = value.validateString(required = true)
                )
            )
        }
    }

    fun updateDefaultPrice(value: String) {
        _state.update {
            it.copy(
                defaultPrice = Input(
                    value = value, errors = value.validateBigDecimal()
                )
            )
        }
    }

    fun updateStock(value: String) {
        _state.update {
            it.copy(
                stock = Input(
                    value = value, errors = value.validateInt()
                )
            )
        }
    }

    fun editProduct() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Editing product")
            assert(_state.value.isValid)
            assert(_state.value.name.value.isNotBlank())
            assert(_state.value.productDto != null)

            updateProductUseCase.exec(
                id = productId,
                name = _state.value.name.value,
                defaultPrice = _state.value.defaultPrice.value.toBigDecimal(),
                stock = _state.value.stock.value.toInt(),
            )
        }
    }
}