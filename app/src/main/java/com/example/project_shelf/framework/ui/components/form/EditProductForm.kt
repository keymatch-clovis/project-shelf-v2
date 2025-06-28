package com.example.project_shelf.framework.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductUiState

@Composable
fun EditProductForm(
    state: State<EditProductUiState>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onNameChange: (value: String) -> Unit,
    onPriceChange: (value: String) -> Unit,
    onCountChange: (value: String) -> Unit,
) {
    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            /// Name
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
                isError = state.value.errors["name"] != null,
                singleLine = true,
                value = state.value.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.name)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.name.isNotEmpty()) {
                        IconButton(onClick = { onNameChange("") }) {
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
                isError = state.value.errors["price"] != null,
                singleLine = true,
                value = state.value.price,
                onValueChange = onPriceChange,
                label = { Text(stringResource(R.string.price)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.price.isNotEmpty()) {
                        IconButton(onClick = { onPriceChange("") }) {
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
                isError = state.value.errors["count"] != null,
                singleLine = true,
                value = state.value.count,
                onValueChange = onCountChange,
                label = { Text(stringResource(R.string.amount)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = ""
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.count.isNotEmpty()) {
                        IconButton(onClick = { onCountChange("") }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
            )
        }
    }
}