package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.city.CitySearchViewModel
import com.example.project_shelf.adapter.view_model.customer.CreateCustomerViewModel
import com.example.project_shelf.framework.ui.components.form.CreateCustomerForm
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerScreen(
    viewModel: CreateCustomerViewModel,
    citySearchViewModel: CitySearchViewModel,
    onDismissRequest: () -> Unit,
) {
    val inputState = viewModel.inputState.collectAsState()
    val validationState = viewModel.validationState.collectAsState()
    val isValid = viewModel.isValid.collectAsState()

    // We need the cities to create a customer.
    val citiesLazyPagingItems = citySearchViewModel.result.collectAsLazyPagingItems()

    // Listen to ViewModel events.
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is CreateCustomerViewModel.Event.Created -> onDismissRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.customer_create)) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_left_solid),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    Button(
                        enabled = isValid.value,
                        onClick = {
                            viewModel.create()
                        },
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        CreateCustomerForm(
            innerPadding = innerPadding,

            name = inputState.value.name,
            nameErrors = validationState.value.nameErrors.map { it.getStringResource() },
            onNameChange = { viewModel.updateName(it) },

            phone = inputState.value.phone,
            phoneErrors = validationState.value.phoneErrors.map { it.getStringResource() },
            onPhoneChange = { viewModel.updatePhone(it) },

            address = inputState.value.address,
            addressErrors = validationState.value.addressErrors.map { it.getStringResource() },
            onAddressChange = { viewModel.updateAddress(it) },

            businessName = inputState.value.businessName,
            businessNameErrors = validationState.value.businessNameErrors.map { it.getStringResource() },
            onBusinessNameChange = { viewModel.updateBusinessName(it) },

            citiesLazyPagingItems = citiesLazyPagingItems,
            onCitySearch = { citySearchViewModel.updateQuery(it) }
        )
    }
}