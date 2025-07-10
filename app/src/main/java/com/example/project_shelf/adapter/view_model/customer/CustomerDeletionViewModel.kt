package com.example.project_shelf.adapter.view_model.customer

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDeletionViewModel @Inject constructor(
    private val repository: CustomerRepository,
) : ViewModel() {
    private val _snackbarState = MutableStateFlow(SnackbarHostState())
    val snackbarState = _snackbarState.asStateFlow()

    val customersMarkedForDeletion = Channel<CustomerDto>(Channel.CONFLATED)

    fun clear() {
        _snackbarState.update { SnackbarHostState() }
    }

    suspend fun startSnackbar(message: String, actionLabel: String) {
        // If I understand this correctly, the channel will only show the latest value. This is very
        // useful, as we don't need to manage the amount of DTOs the channel or list has. We just
        // care that the last DTO shown, is the latest the user has deleted. If any other snackbar
        // is still showing, it can call for the unmarking of the product by itself.
        // https://www.baeldung.com/kotlin/channels
        customersMarkedForDeletion.receiveAsFlow().collect {
            var result = _snackbarState.value.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Long,
                withDismissAction = true,
            )

            when (result) {
                SnackbarResult.ActionPerformed -> unsetCustomerPendingForDeletion(it)
                // Just ignore the value, as we are using a conflated channel, we are sure the
                // value will show just once. This will make that the channel always has the
                // latest value, even if the user dismissed or ignored the snackbar, but this is
                // fine. We can just ignore it.
                SnackbarResult.Dismissed -> {}
            }
        }
    }

    fun setCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Setting customer pending for deletion: $dto")
            repository.setPendingForDeletion(dto.id)
            customersMarkedForDeletion.send(dto)
        }
    }

    private fun unsetCustomerPendingForDeletion(dto: CustomerDto) {
        viewModelScope.launch {
            Log.d("VIEW-MODEL", "Unsetting customer pending for deletion: $dto")
            repository.unsetPendingForDeletion(dto.id)
        }
    }
}