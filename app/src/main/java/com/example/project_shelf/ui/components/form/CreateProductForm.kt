package com.example.project_shelf.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.adapter.view_model.CreateProductViewModel
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductViewModel

@Composable
fun CreateProductForm(
    viewModel: CreateProductViewModel,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    val state = viewModel.uiState.collectAsState()

    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            /// Name
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
                isError = state.value.nameErrors.isNotEmpty(),
                singleLine = true,
                value = state.value.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(R.string.name)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = state.value.nameErrors.map { stringResource(it) }.firstOrNull()
                                ?: ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.name.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateName("") }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
            )
            /// Price
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal,
                ),
                isError = state.value.priceErrors.isNotEmpty(),
                singleLine = true,
                value = state.value.price,
                onValueChange = { viewModel.updatePrice(it) },
                label = { Text(stringResource(R.string.price)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = state.value.priceErrors.map { stringResource(it) }.firstOrNull()
                                ?: ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.price.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updatePrice("") }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
            )
            /// Count
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal,
                ),
                isError = state.value.countErrors.isNotEmpty(),
                singleLine = true,
                value = state.value.count,
                onValueChange = { viewModel.updateCount(it) },
                label = { Text(stringResource(R.string.amount)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = state.value.countErrors.map { stringResource(it) }.firstOrNull()
                                ?: ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.count.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateCount("") }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
            )
        }
    }
}
