package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.product.CreateProductViewModel
import com.example.project_shelf.framework.ui.components.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import com.example.project_shelf.framework.ui.util.CurrencyVisualTransformation
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel,
    onCreated: (ProductDto) -> Unit,
    onDismissed: () -> Unit,
) {
    val name = viewModel.inputState.name.rawValue.collectAsState()
    val nameErrors = viewModel.inputState.name.errors.collectAsState()

    val price = viewModel.inputState.price.rawValue.collectAsState()
    val priceErrors = viewModel.inputState.price.errors.collectAsState()

    val stock = viewModel.inputState.stock.rawValue.collectAsState()
    val stockErrors = viewModel.inputState.stock.errors.collectAsState()

    val isValid = viewModel.isValid.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is CreateProductViewModel.Event.Created -> onCreated(it.dto)
            }
        }
    }

    /// Related to initial focus.
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.product_create)) },
                navigationIcon = {
                    IconButton(onClick = onDismissed) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
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
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                /// Name
                CustomTextField(
                    value = name.value ?: "",
                    modifier = Modifier.focusRequester(focusRequester),
                    required = true,
                    onValueChange = { viewModel.updateName(it) },
                    onClear = { viewModel.updateName("") },
                    label = R.string.name,
                    errors = nameErrors.value.map { it.getStringResource() },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    ),
                )
                /// Default price
                CustomTextField(
                    value = price.value ?: "",
                    visualTransformation = CurrencyVisualTransformation(Locale.getDefault()),
                    onValueChange = {
                        viewModel.updatePrice(it)
                    },
                    onClear = { viewModel.updatePrice("") },
                    label = R.string.default_price,
                    errors = priceErrors.value.map { it.getStringResource() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next
                    ),
                )
                /// Stock
                CustomTextField(
                    value = stock.value ?: "",
                    onValueChange = { viewModel.updateStock(it) },
                    onClear = { viewModel.updateStock("") },
                    label = R.string.amount,
                    errors = stockErrors.value.map { it.getStringResource() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                    ),
                )
            }
        }
    }
}