package com.example.project_shelf.adapter.view_model.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.app.use_case.product.SetProductPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.product.UnsetProductPendingForDeletionUseCase
import com.example.project_shelf.common.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductViewModelState {
    data class State(
        val selectedProductId: Id? = null,
        val productsMarkedForDeletion: List<Id> = emptyList(),
    )
}

@HiltViewModel()
class ProductViewModel @Inject constructor(
    private val setProductPendingForDeletionUseCase: SetProductPendingForDeletionUseCase,
    private val unsetProductPendingForDeletionUseCase: UnsetProductPendingForDeletionUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProductViewModelState.State())
    val state = _state.asStateFlow()

    fun setSelectedProductId(id: Id?) {
        _state.update { it.copy(selectedProductId = id) }
    }

    fun getSelectedProductId(): Id {
        return _state.value.selectedProductId!!.also {
            _state.update { it.copy(selectedProductId = null) }
        }
    }

    fun setProductPendingForDeletion(id: Id) {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Setting product pending for deletion: $id")
            setProductPendingForDeletionUseCase.exec(id)
            _state.update { it.copy(productsMarkedForDeletion = it.productsMarkedForDeletion + id) }
        }
    }

    fun unsetProductPendingForDeletion(id: Id) {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Unsetting product pending for deletion: $id")
            unsetProductPendingForDeletionUseCase.exec(id)
            removeProductFromMarkedForDeletion(id)
        }
    }

    fun removeProductFromMarkedForDeletion(id: Id) {
        _state.update { it.copy(productsMarkedForDeletion = it.productsMarkedForDeletion - id) }
    }
}