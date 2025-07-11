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
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.framework.ui.components.CustomTextField

@Composable
fun CreateCustomerForm(
    name: String,
    nameErrors: List<Int>,
    onNameChange: (value: String) -> Unit,
    phone: String,
    phoneErrors: List<Int>,
    onPhoneChange: (value: String) -> Unit,
    address: String,
    addressErrors: List<Int>,
    onAddressChange: (value: String) -> Unit,
    businessName: String,
    businessNameErrors: List<Int>,
    onBusinessNameChange: (value: String) -> Unit,
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
                value = name,
                onValueChange = onNameChange,
                label = R.string.name,
                errors = nameErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Phone
            CustomTextField(
                required = true,
                value = phone,
                onValueChange = onPhoneChange,
                label = R.string.phone,
                errors = phoneErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Address
            CustomTextField(
                required = true,
                value = address,
                onValueChange = onAddressChange,
                label = R.string.address,
                errors = addressErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// Business Name
            CustomTextField(
                value = businessName,
                onValueChange = onBusinessNameChange,
                label = R.string.business_name,
                errors = businessNameErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done
                ),
            )
        }
    }
}
