package com.example.project_shelf.adapter.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CreateProductUiState(
    val name: String = "",
    val price: String = "",
    val count: String = "",

    val nameErrors: MutableList<String> = mutableListOf()
)

class CreateProductViewModel : ViewModel() {
    private val innerState = MutableStateFlow(CreateProductUiState())
    val state = innerState.asStateFlow()

    fun updateName(name: String) {
        Log.d(CreateProductViewModel::class.simpleName, "Update Product Name: $name")
        innerState.value = innerState.value.copy(name = name.uppercase())
    }

    fun updatePrice(price: String) {
        Log.d(CreateProductViewModel::class.simpleName, "Update Product Price: $price")
        innerState.value = innerState.value.copy(price = price)
    }

    fun updateCount(count: String) {
        Log.d(CreateProductViewModel::class.simpleName, "Update Product Count: $count")
        innerState.value = innerState.value.copy(count = count)
    }
}