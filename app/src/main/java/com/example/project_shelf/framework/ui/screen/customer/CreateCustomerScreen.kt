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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CityFilterDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.customer.CreateCustomerScreenViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.CityFilterListItem
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomerScreen(
    state: CreateCustomerScreenViewModel.State,
    callback: CreateCustomerScreenViewModel.Callback,
    citySearchState: SearchExtension.State,
    citySearchCallback: SearchExtension.Callback,
    citySearchResult: Flow<PagingData<CityFilterDto>>,
) {
    /// Search related
    val citySearchItems = citySearchResult.collectAsLazyPagingItems()

    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.customer_create)) },
                    navigationIcon = {
                        IconButton(onClick = { callback.onDismissRequest() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        Button(
                            enabled = state.isValid,
                            onClick = { callback.onCreateRequest() },
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
                        value = state.name.value ?: "",
                        onValueChange = { callback.onNameChange(it) },
                        label = R.string.name,
                        errors = state.name.errors.map { it.getStringResource() },
                        onClear = { callback.onNameChange("") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// Phone
                    CustomTextField(
                        required = true,
                        value = state.phone.value ?: "",
                        onValueChange = { callback.onPhoneChange(it) },
                        onClear = { callback.onPhoneChange("") },
                        label = R.string.phone,
                        errors = state.phone.errors.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// Address
                    CustomTextField(
                        label = R.string.address,
                        value = state.address.value ?: "",
                        onValueChange = { callback.onAddressChange(it) },
                        onClear = { callback.onAddressChange("") },
                        errors = state.address.errors.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// City Search Input
                    CustomTextField(
                        label = R.string.city,
                        required = true,
                        readOnly = true,
                        value = state.city.value?.name ?: "",
                        onClick = { callback.openCitySearch() },
                        errors = state.city.errors.map { it.getStringResource() },
                    )
                    /// Business Name
                    CustomTextField(
                        label = R.string.business_name,
                        value = state.businessName.value ?: "",
                        onValueChange = { callback.onBusinessNameChange(it) },
                        onClear = { callback.onBusinessNameChange("") },
                        errors = state.businessName.errors.map { it.getStringResource() },
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
            visible = state.isShowingCitySearch,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CustomSearchBar<CityFilterDto>(
                query = citySearchState.query,
                onQueryChange = { citySearchCallback.onUpdateQuery(it) },
                expanded = state.isShowingCitySearch,
                onExpandedChange = { if (it) callback.openCitySearch() else callback.closeCitySearch() },
                lazyPagingItems = citySearchItems,
                onSearch = {
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    citySearchItems.takeIf { it.itemCount > 0 }
                        ?.peek(0)
                        ?.let {
                            callback.onCityChange(it)
                        }
                },
            ) {
                CityFilterListItem(
                    dto = it,
                    onClick = {
                        callback.onCityChange(it)
                        callback.closeCitySearch()
                    },
                )
            }
        }
    }
}