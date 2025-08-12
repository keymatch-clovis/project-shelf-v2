package com.example.project_shelf.framework.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField

@Composable
fun EditProductForm(
    name: String,
    price: String,
    stock: String,
    nameErrors: List<Int>,
    priceErrors: List<Int>,
    stockErrors: List<Int>,
    onNameChange: (value: String) -> Unit,
    onPriceChange: (value: String) -> Unit,
    onStockChange: (value: String) -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            /// Name
            CustomTextField(
                value = name,
                errors = nameErrors,
                onValueChange = onNameChange,
                label = R.string.name,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Default price
            CustomTextField(
                value = price,
                errors = priceErrors,
                onValueChange = onPriceChange,
                label = R.string.default_price,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next
                ),
            )
            /// Stock
            CustomTextField(
                value = stock,
                errors = stockErrors,
                onValueChange = onStockChange,
                label = R.string.amount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                ),
            )
        }
    }
}