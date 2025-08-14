package com.example.project_shelf.adapter.view_model.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DeletionExtension<T : Any>(
    private val scope: CoroutineScope,
    private val setItemPendingForDeletion: suspend (T) -> Unit,
    private val unsetItemPendingForDeletion: suspend (T) -> Unit,
) {
    data class State<T : Any>(
        val isLoading: Boolean = false,
        val itemsPendingForDeletion: List<T> = emptyList(),
    )

    data class Callback<T : Any>(
        val onSetItemPendingForDeletion: (T) -> Unit,
        val onUnsetItemPendingForDeletion: (T) -> Unit,
        val onDismissItemPendingForDeletion: (T) -> Unit,
    )

    private val _state = MutableStateFlow(State<T>())
    val state = _state.asStateFlow()

    fun onSetItemPendingForDeletion(item: T) {
        _state.update { it.copy(isLoading = true) }
        scope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Setting item pending for deletion: $item")
            setItemPendingForDeletion(item)

            _state.update {
                it.copy(
                    itemsPendingForDeletion = it.itemsPendingForDeletion + item,
                    isLoading = false,
                )
            }
        }
    }

    fun onUnsetItemPendingForDeletion(item: T) {
        _state.update { it.copy(isLoading = true) }
        scope.launch(context = Dispatchers.IO) {
            Log.d("VIEW-MODEL", "Unsetting item pending for deletion: $item")
            unsetItemPendingForDeletion(item)

            _state.update {
                it.copy(
                    itemsPendingForDeletion = it.itemsPendingForDeletion - item,
                    isLoading = false,
                )
            }
        }
    }

    fun onDismissItemPendingForDeletion(item: T) {
        _state.update { it.copy(itemsPendingForDeletion = it.itemsPendingForDeletion - item) }
    }
}

fun <T : Any> ViewModel.addDeletionExtension(
    setItemPendingForDeletion: suspend (T) -> Unit,
    unsetItemPendingForDeletion: suspend (T) -> Unit,
): DeletionExtension<T> {
    return DeletionExtension(
        scope = viewModelScope,
        setItemPendingForDeletion = setItemPendingForDeletion,
        unsetItemPendingForDeletion = unsetItemPendingForDeletion,
    )
}