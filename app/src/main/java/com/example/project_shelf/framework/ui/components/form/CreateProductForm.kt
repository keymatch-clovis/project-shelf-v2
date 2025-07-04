package com.example.project_shelf.framework.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.framework.ui.components.CustomTextField

@Composable
fun CreateProductForm(
    nameInputValue: String,
    defaultPriceInputValue: String,
    stockInputValue: String,
    nameErrors: List<Int>,
    defaultPriceErrors: List<Int>,
    stockErrors: List<Int>,
    onNameChange: (value: String) -> Unit,
    onPriceChange: (value: String) -> Unit,
    onStockChange: (value: String) -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp),
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
            CustomTextField(
                modifier = Modifier.focusRequester(focusRequester),
                required = true,
                value = nameInputValue,
                onValueChange = onNameChange,
                label = R.string.name,
                errors = nameErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Default price
            CustomTextField(
                value = defaultPriceInputValue,
                onValueChange = onPriceChange,
                label = R.string.default_price,
                errors = defaultPriceErrors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next
                ),
            )
            /// Stock
            CustomTextField(
                value = stockInputValue,
                onValueChange = onStockChange,
                label = R.string.amount,
                errors = stockErrors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                ),
            )
        }
    }
}
