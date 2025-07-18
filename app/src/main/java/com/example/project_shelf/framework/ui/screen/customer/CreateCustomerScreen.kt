package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CityDto
import com.example.project_shelf.adapter.view_model.customer.CreateCustomerViewModel
import com.example.project_shelf.framework.ui.components.form.CreateCustomerForm
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerScreen(
    viewModel: CreateCustomerViewModel,
    onDismissRequest: () -> Unit,
) {
    val name = viewModel.inputState.name.rawValue.collectAsState()
    val nameErrors = viewModel.inputState.name.errors.collectAsState()

    val phone = viewModel.inputState.phone.rawValue.collectAsState()
    val phoneErrors = viewModel.inputState.phone.errors.collectAsState()

    val address = viewModel.inputState.address.rawValue.collectAsState()
    val addressErrors = viewModel.inputState.address.errors.collectAsState()

    val businessName = viewModel.inputState.businessName.rawValue.collectAsState()
    val businessNameErrors = viewModel.inputState.businessName.errors.collectAsState()

    val city = viewModel.inputState.city.collectAsState()
    val cityErrors = viewModel.inputState.cityErrors.collectAsState()

    val isValid = viewModel.isValid.collectAsState()

    /// Related to city search.
    val citySearchFocusRequester = remember { FocusRequester() }
    val citiesLazyPagingItems = viewModel.citySearchResult.collectAsLazyPagingItems()
    val cityQuery = viewModel.cityQuery.collectAsState()
    val onCitySearchedFocusRequester = remember { FocusRequester() }
    var showCitySearchBar by remember { mutableStateOf(false) }

    // Listen to ViewModel events.
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is CreateCustomerViewModel.Event.Created -> onDismissRequest()
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
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left_solid),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Button(
                            enabled = isValid.value,
                            onClick = {
                                viewModel.create()
                            },
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    },
                )
            },
        ) { innerPadding ->
            CreateCustomerForm(
                innerPadding = innerPadding,

                name = name.value,
                nameErrors = nameErrors.value.map { it.getStringResource() },
                onNameChange = { viewModel.updateName(it) },

                phone = phone.value,
                phoneErrors = phoneErrors.value.map { it.getStringResource() },
                onPhoneChange = { viewModel.updatePhone(it) },

                address = address.value,
                addressErrors = addressErrors.value.map { it.getStringResource() },
                onAddressChange = { viewModel.updateAddress(it) },

                businessName = businessName.value,
                businessNameErrors = businessNameErrors.value.map { it.getStringResource() },
                onBusinessNameChange = { viewModel.updateBusinessName(it) },

                city = city.value,
                cityErrors = cityErrors.value.map { it.getStringResource() },
                onCitySearch = { showCitySearchBar = true },
            )
        }

        // Search City Search Bar.
        // NOTE:
        //  I have to create this box here, as I don't really know if there is a way to put both a
        //  search bar, and the results in a same component. I feel I really need to use two of them
        //  separated.
        AnimatedVisibility(
            visible = showCitySearchBar,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            // NOTE:
            //  Idk if this is working correctly, but I guess I have no other way of knowing when
            //  this element is already in the composition tree.
            LaunchedEffect(Unit) {
                citySearchFocusRequester.requestFocus()
                citySearchFocusRequester.captureFocus()
            }

            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.focusRequester(citySearchFocusRequester),
                        query = cityQuery.value,
                        // If the user clicks the search button, we will assume it wants to select
                        // the first-most item.
                        // NOTE:
                        //  I have not found a way to leave this to the viewmodel, as the view
                        //  should not be dictating how the items are being selected. Or is it?
                        //  Idk, I'll leave it like this for now, maybe in the future we'll figure
                        //  out a better way.
                        onSearch = {
                            var item: CityDto? = null
                            if (citiesLazyPagingItems.itemCount > 0) {
                                item = citiesLazyPagingItems.peek(0)
                            }

                            viewModel.updateCity(item)
                            showCitySearchBar = false
                        },
                        onQueryChange = { viewModel.updateCityQuery(it) },
                        expanded = showCitySearchBar,
                        onExpandedChange = { showCitySearchBar = it },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.magnifying_glass_solid),
                                contentDescription = null,
                            )
                        },
                        placeholder = { Text(stringResource(R.string.search)) },
                    )
                },
                expanded = showCitySearchBar,
                onExpandedChange = { showCitySearchBar = it },
            ) {
                LazyColumn {
                    items(count = citiesLazyPagingItems.itemCount) { index ->
                        citiesLazyPagingItems[index]?.let {
                            Surface(
                                onClick = {}) {
                                ListItem(modifier = Modifier.fillMaxWidth(), headlineContent = {
                                    Text(
                                        style = MaterialTheme.typography.bodyMedium,
                                        text = it.name,
                                    )
                                }, supportingContent = {
                                    Text(
                                        style = MaterialTheme.typography.bodyMedium,
                                        text = it.department,
                                    )
                                })
                            }
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