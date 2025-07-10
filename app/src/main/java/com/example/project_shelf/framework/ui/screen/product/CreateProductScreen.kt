package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.product.CreateProductViewModel
import com.example.project_shelf.framework.ui.components.form.CreateProductForm
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val nameState = viewModel.name.collectAsState()
    val inputState = viewModel.inputState.collectAsState()
    val validationState = viewModel.validationState.collectAsState()
    val isValid = viewModel.isValid.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is CreateProductViewModel.Event.ProductCreated -> onDismissRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.product_create)) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
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
        CreateProductForm(
            innerPadding = innerPadding,

            name = nameState.value,
            nameErrors = validationState.value.nameErrors.map { it.getStringResource() },
            onNameChange = { viewModel.updateName(it) },

            price = inputState.value.price,
            priceErrors = validationState.value.priceErrors.map { it.getStringResource() },
            onPriceChange = { viewModel.updatePrice(it) },

            stock = inputState.value.stock,
            stockErrors = validationState.value.stockErrors.map { it.getStringResource() },
            onStockChange = { viewModel.updateStock(it) },
        )
    }
}