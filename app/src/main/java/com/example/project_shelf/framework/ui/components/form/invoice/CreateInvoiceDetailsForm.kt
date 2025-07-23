package com.example.project_shelf.framework.ui.components.form.invoice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.adapter.view_model.util.Input
import com.example.project_shelf.framework.ui.components.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun CreateInvoiceDetailsForm(
    customerInput: Input<CustomerFilterDto, CustomerFilterDto>,
    emitter: MutableSharedFlow<CreateInvoiceViewModel.Event>,
) {
    /// Related to event emitting
    val scope = rememberCoroutineScope()

    /// Customer related
    val customer = customerInput.rawValue.collectAsState()
    val customerErrors = customerInput.errors.collectAsState()

    Column(
        // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        /// Number
        CustomTextField(
            readOnly = true,
            label = R.string.number,
            value = "1",
        )
        /// Customer
        CustomTextField(
            required = true,
            label = R.string.customer,
            value = customer.value?.name ?: "",
            readOnly = true,
            onClick = {
                scope.launch { emitter.emit(CreateInvoiceViewModel.Event.OpenSearchCustomer) }
            },
            errors = customerErrors.value.map { it.getStringResource() },
        )
    }
}