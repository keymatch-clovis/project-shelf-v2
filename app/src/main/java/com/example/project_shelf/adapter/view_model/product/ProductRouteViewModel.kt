package com.example.project_shelf.adapter.view_model.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.view_model.common.addDeletionExtension
import com.example.project_shelf.adapter.view_model.common.addSearchExtension
import com.example.project_shelf.app.use_case.product.GetProductsUseCase
import com.example.project_shelf.app.use_case.product.SearchProductsUseCase
import com.example.project_shelf.app.use_case.product.SetProductPendingForDeletionUseCase
import com.example.project_shelf.app.use_case.product.UnsetProductPendingForDeletionUseCase
import com.example.project_shelf.common.Id
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel()
class ProductRouteViewModel @Inject constructor(
    getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val setProductPendingForDeletionUseCase: SetProductPendingForDeletionUseCase,
    private val unsetProductPendingForDeletionUseCase: UnsetProductPendingForDeletionUseCase,
) : ViewModel() {
    data class State(
        val selectedProduct: Id? = null,
    )

    data class Callback(
        val onRequestCreateProduct: () -> Unit,
        val onRequestOpenProduct: (productId: Id) -> Unit,
    )

    /// State related
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    /// List related
    val products: Flow<PagingData<ProductDto>> = getProductsUseCase.exec()
        .map { it.map { it.toDto() } }
        .cachedIn(viewModelScope)

    /// Extensions
    val searchExtension = addSearchExtension {
        searchProductsUseCase.exec(it)
            .map { it.map { it.toDto() } }
    }

    val deletionExtension = addDeletionExtension<Id>(
        setItemPendingForDeletion = { setProductPendingForDeletionUseCase.exec(it) },
        unsetItemPendingForDeletion = { unsetProductPendingForDeletionUseCase.exec(it) },
    )

    /// Update methods
    fun setSelectedProduct(id: Id) {
        _state.update { it.copy(selectedProduct = id) }
    }

    fun consumeSelectedProduct(): Id {
        return _state.value.selectedProduct!!.also {
            _state.update { it.copy(selectedProduct = null) }
        }
    }
}