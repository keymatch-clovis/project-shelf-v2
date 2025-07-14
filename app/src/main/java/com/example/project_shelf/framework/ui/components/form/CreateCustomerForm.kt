package com.example.project_shelf.framework.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CityDto
import com.example.project_shelf.framework.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
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
    city: CityDto?,
    cityErrors: List<Int>,
    onCitySearch: () -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            /// Name
            CustomTextField(
                required = true,
                value = name,
                onValueChange = onNameChange,
                label = R.string.name,
                errors = nameErrors,
                onClear = { onNameChange("") },
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
                onClear = { onPhoneChange("") },
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
                onClear = { onAddressChange("") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next
                ),
            )
            /// City Search Input
            CustomTextField(
                required = true,
                label = R.string.city,
                value = city?.name ?: "",
                onValueChange = {},
                readOnly = true,
                onClick = {
                    onCitySearch()
                },
                errors = cityErrors,
            )
            /// Business Name
            CustomTextField(
                value = businessName,
                onValueChange = onBusinessNameChange,
                onClear = { onBusinessNameChange("") },
                label = R.string.business_name,
                errors = businessNameErrors,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done
                ),
            )
        }
    }
}
