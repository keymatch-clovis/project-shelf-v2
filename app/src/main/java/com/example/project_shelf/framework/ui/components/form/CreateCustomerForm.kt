package com.example.project_shelf.framework.ui.components.form

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
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
    citiesLazyPagingItems: LazyPagingItems<CityFilterDto>,
    onCitySearch: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp),
) {
    val citySearchFocusRequester = remember { FocusRequester() }

    /// TEST
    var focusManager = LocalFocusManager.current
    var isVisible by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val test = remember { MutableInteractionSource() }
    val (businessNameFocusRef) = remember { FocusRequester.createRefs() }

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
                value = businessName,
                onValueChange = onBusinessNameChange,
                readOnly = true,
                onClick = {
                    if (it) {
                        isVisible = true
                    }
                },
                label = R.string.city,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done
                ),
            )
            /// Business Name
            CustomTextField(
                modifier = Modifier.focusRequester(businessNameFocusRef),
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

        // NOTE:
        //  I have to create this box here, as I don't really know if there is a way to put both a
        //  search bar, and the results in a same component. I feel I really need to use two of them
        //  separated.
        // This is related to the customer city search.
        AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
            LaunchedEffect(Unit) {
                citySearchFocusRequester.requestFocus()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            modifier = Modifier
                                .focusRequester(citySearchFocusRequester)
                                .focusProperties { next = businessNameFocusRef },
                            query = query,
                            onQueryChange = {
                                query = it
                                onCitySearch(query)
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = { isVisible = false },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.xmark_solid),
                                        contentDescription = null,
                                    )
                                }
                            },
                            placeholder = {
                                Text("Search a city")
                            },
                            onSearch = {
                                isVisible = false
                                focusManager.moveFocus(FocusDirection.Next)
                            },
                            expanded = isVisible,
                            onExpandedChange = { isVisible = it },
                        )
                    },
                    expanded = isVisible,
                    onExpandedChange = { isVisible = it },
                ) {
                    LazyColumn {
                        items(count = citiesLazyPagingItems.itemCount) { index ->
                            citiesLazyPagingItems[index]?.let {
                                Text(it.name)
                            }

                            if (index < citiesLazyPagingItems.itemCount - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}
