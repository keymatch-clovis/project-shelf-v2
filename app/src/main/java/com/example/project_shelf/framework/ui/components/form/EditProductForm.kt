package com.example.project_shelf.framework.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductUiState
import com.example.project_shelf.framework.ui.components.CustomTextField

@Composable
fun EditProductForm(
    state: State<EditProductUiState>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onNameChange: (value: String) -> Unit,
    onPriceChange: (value: String) -> Unit,
    onCountChange: (value: String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            /// Name
            CustomTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = state.value.rawName,
                onValueChange = onNameChange,
                label = R.string.name,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Default price
            CustomTextField(
                value = state.value.rawDefaultPrice,
                onValueChange = onPriceChange,
                label = R.string.default_price,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next
                ),
            )
            /// Count
            CustomTextField(
                value = state.value.rawStock,
                onValueChange = onCountChange,
                label = R.string.amount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                ),
            )
        }
    }
}