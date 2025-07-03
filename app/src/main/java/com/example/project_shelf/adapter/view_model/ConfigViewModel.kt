package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun removeAllData(onRemoved: suspend () -> Unit) {
        viewModelScope.launch {
            productRepository.removeAll()
            onRemoved()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun loadTestProducts(onLoaded: suspend () -> Unit) {
        viewModelScope.launch {
            repeat(500) {
                productRepository.createProduct(
                    name = Uuid.random().toString(),
                    price = Random.nextDouble(1000.0).toBigDecimal(),
                    stock = Random.nextInt(100),
                )
            }
            onLoaded()
        }
    }
}