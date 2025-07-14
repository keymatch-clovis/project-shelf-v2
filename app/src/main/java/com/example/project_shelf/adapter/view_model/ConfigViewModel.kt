package com.example.project_shelf.adapter.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun removeAllData(onRemoved: suspend () -> Unit) {
        viewModelScope.launch {
            productRepository.deleteAll()
            onRemoved()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun loadTestProducts(onLoaded: suspend () -> Unit) {
        viewModelScope.launch {
            repeat(50) {
                productRepository.create(
                    name = Uuid.random().toString(),
                    price = Random.nextInt(1000).toBigDecimal(),
                    stock = Random.nextInt(100),
                )
            }
            onLoaded()
        }
    }
}