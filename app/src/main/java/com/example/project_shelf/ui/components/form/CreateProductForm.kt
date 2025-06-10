package com.example.project_shelf.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.CreateProductViewModel
import androidx.compose.ui.unit.dp

private class CreateProductFormParameterProvider :
    PreviewParameterProvider<CreateProductViewModel> {
    override val values = sequenceOf(
        CreateProductViewModel()
    )
}

@Preview
@Composable
fun CreateProductForm(
    @PreviewParameter(CreateProductFormParameterProvider::class) viewModel: CreateProductViewModel
) {
    val state = viewModel.state.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
                singleLine = true,
                value = state.value.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(R.string.name)) },
                trailingIcon = {
                    if (state.value.name.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateName("") }) {
                            Icon(
                                Icons.Rounded.Clear,
                                contentDescription = null,
                            )
                        }
                    }
                })
            Text(
                text = "${state.value.name.length} / 255",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.End,
            )
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, keyboardType = KeyboardType.Decimal
            ),
            value = state.value.price,
            onValueChange = { viewModel.updatePrice(it) },
            label = { Text(stringResource(R.string.price)) },
            trailingIcon = {
                if (state.value.name.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updatePrice("") }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = null,
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.AttachMoney,
                    contentDescription = null,
                )
            })
        TextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal
            ),
            value = state.value.count,
            onValueChange = { viewModel.updateCount(it) },
            label = { Text(stringResource(R.string.amount)) },
            trailingIcon = {
                if (state.value.name.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateCount("") }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = null,
                        )
                    }
                }
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Tag,
                    contentDescription = null,
                )
            })
    }
}