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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.CreateProductUiState
import com.example.project_shelf.common.BlankValueException

@Composable
fun CreateProductForm(
    state: State<CreateProductUiState>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onUpdateName: (value: String) -> Unit,
    onUpdatePrice: (value: String) -> Unit,
    onUpdateCount: (value: String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            /// Name
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
                isError = state.value.errors["name"] != null,
                singleLine = true,
                value = state.value.name,
                onValueChange = onUpdateName,
                label = { Text(stringResource(R.string.name)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            // TODO: Ugly asf, maybe abstract this?
                            text = if (state.value.errors["name"] == null) "" else when (state.value.errors["name"]!!::class) {
                                BlankValueException::class -> stringResource(R.string.err_value_required)
                                else -> stringResource(R.string.err_invalid_value)
                            }
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.name.isNotEmpty()) {
                        IconButton(onClick = { onUpdateName("") }) {
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
                onValueChange = onUpdatePrice,
                label = { Text(stringResource(R.string.price)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = when (state.value.errors["price"]) {
                                BlankValueException::class -> ""
                                NumberFormatException::class -> ""
                                else -> ""
                            }
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.price.isNotEmpty()) {
                        IconButton(onClick = { onUpdatePrice("") }) {
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
                onValueChange = onUpdateCount,
                label = { Text(stringResource(R.string.amount)) },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = when (state.value.errors["count"]) {
                                BlankValueException::class -> "aoeu"
                                NumberFormatException::class -> "test"
                                else -> "puta"
                            }
                        )
                    }
                },
                trailingIcon = {
                    if (state.value.count.isNotEmpty()) {
                        IconButton(onClick = { onUpdateCount("") }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
            )
        }
    }
}
