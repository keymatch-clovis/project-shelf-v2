package com.example.project_shelf.framework.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import com.example.project_shelf.framework.ui.util.CurrencyVisualTransformation
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvoiceProductDialog(
    name: String,
    price: Input<String>,
    onChangePrice: (String?) -> Unit,
    onDismissRequest: () -> Unit,
    onAddRequest: () -> Unit,
) {
    // https://m3.material.io/components/dialogs/specs
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // https://m3.material.io/components/dialogs/specs#9a8c226b-19fa-4d6b-894e-e7d5ca9203e8
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = stringResource(R.string.product_add),
                )
                Spacer(Modifier.height(16.dp))
                /// Name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CustomTextField(
                        label = R.string.name,
                        readOnly = true,
                        required = true,
                        value = name,
                    )
                    CustomTextField(
                        label = R.string.price,
                        value = price.value,
                        visualTransformation = CurrencyVisualTransformation(Locale.getDefault()),
                        onValueChange = { onChangePrice(it) },
                        onClear = { onChangePrice(null) },
                        errors = price.errors.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next
                        ),
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = { onAddRequest() }) {
                        Text(stringResource(R.string.accept))
                    }
                }
            }
        }
    }
}