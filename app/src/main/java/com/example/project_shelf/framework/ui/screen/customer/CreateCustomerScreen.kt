package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.view_model.customer.CreateCustomerViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.CustomTextField
import com.example.project_shelf.framework.ui.components.list_item.CityFilterListItem
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerScreen(
    viewModel: CreateCustomerViewModel,
    onCreated: (CustomerDto) -> Unit,
    onDismissed: () -> Unit,
) {
    val name = viewModel.inputState.name.rawValue.collectAsState()
    val nameErrors = viewModel.inputState.name.errors.collectAsState()

    val phone = viewModel.inputState.phone.rawValue.collectAsState()
    val phoneErrors = viewModel.inputState.phone.errors.collectAsState()

    val address = viewModel.inputState.address.rawValue.collectAsState()
    val addressErrors = viewModel.inputState.address.errors.collectAsState()

    val businessName = viewModel.inputState.businessName.rawValue.collectAsState()
    val businessNameErrors = viewModel.inputState.businessName.errors.collectAsState()

    val city = viewModel.inputState.city.rawValue.collectAsState()
    val cityErrors = viewModel.inputState.city.errors.collectAsState()

    val isValid = viewModel.isValid.collectAsState()

    /// Related to city search.
    var showCitySearchBar = viewModel.showCitySearchBar.collectAsState()
    val cityQuery = viewModel.citySearch.query.collectAsState()
    val citySearchItems = viewModel.citySearch.result.collectAsLazyPagingItems()

    // Listen to ViewModel events.
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is CreateCustomerViewModel.Event.Created -> onCreated(it.dto)
            }
        }
    }

    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.customer_create)) },
                    navigationIcon = {
                        IconButton(onClick = onDismissed) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Button(
                            enabled = isValid.value,
                            onClick = { viewModel.create() },
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    },
                )
            },
        ) { innerPadding ->
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
                        value = name.value ?: "",
                        onValueChange = { viewModel.updateName(it) },
                        label = R.string.name,
                        errors = nameErrors.value.map { it.getStringResource() },
                        onClear = { viewModel.updateName("") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// Phone
                    CustomTextField(
                        required = true,
                        value = phone.value ?: "",
                        onValueChange = { viewModel.updatePhone(it) },
                        label = R.string.phone,
                        errors = phoneErrors.value.map { it.getStringResource() },
                        onClear = { viewModel.updatePhone("") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// Address
                    CustomTextField(
                        required = true,
                        value = address.value ?: "",
                        onValueChange = { viewModel.updateAddress(it) },
                        label = R.string.address,
                        errors = addressErrors.value.map { it.getStringResource() },
                        onClear = { viewModel.updateAddress("") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// City Search Input
                    CustomTextField(
                        required = true,
                        label = R.string.city,
                        value = city.value?.name ?: "",
                        readOnly = true,
                        onClick = { viewModel.openCitySearchBar() },
                        errors = cityErrors.value.map { it.getStringResource() },
                    )
                    /// Business Name
                    CustomTextField(
                        value = businessName.value ?: "",
                        onValueChange = { viewModel.updateBusinessName(it) },
                        onClear = { viewModel.updateBusinessName("") },
                        label = R.string.business_name,
                        errors = businessNameErrors.value.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                    )
                }
            }
        }

        /// Search City Search Bar.
        AnimatedVisibility(
            visible = showCitySearchBar.value,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CustomSearchBar<CityFilterDto>(
                query = cityQuery.value,
                onQueryChange = { viewModel.citySearch.updateQuery(it) },
                expanded = showCitySearchBar.value,
                onExpandedChange = {
                    if (it) viewModel.openCitySearchBar() else viewModel.closeCitySearchBar()
                },
                onSearch = {
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    citySearchItems.takeIf { it.itemCount > 0 }?.peek(0)
                        ?.let { viewModel.updateCity(it) }

                    viewModel.closeCitySearchBar()
                },
                lazyPagingItems = citySearchItems,
            ) {
                CityFilterListItem(
                    dto = it,
                    onClick = {
                        viewModel.updateCity(it)
                        viewModel.closeCitySearchBar()
                    },
                )
            }
        }
    }
}