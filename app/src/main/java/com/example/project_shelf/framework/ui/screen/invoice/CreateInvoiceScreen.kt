package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.DraftIndicator
import com.example.project_shelf.framework.ui.components.dialog.LoadingDialog
import com.example.project_shelf.framework.ui.components.form.invoice.CreateInvoiceDetailsForm
import com.example.project_shelf.framework.ui.components.form.invoice.CreateInvoiceProductsForm
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.ProductFilterListItem
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import com.example.project_shelf.framework.ui.common.CurrencyVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    draftViewModel: InvoiceDraftViewModel,
    viewModel: CreateInvoiceViewModel,
    onRequestDismiss: () -> Unit,
) {
    /// Navigation related
    val navHostController = rememberNavController()
    val startDestination = CreateInvoiceDestination.DETAILS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    /// Related to customer search
    val showCustomerSearchBar = viewModel.showCustomerSearchBar.collectAsState()
    val customerQuery = viewModel.customerSearch.query.collectAsState()
    val customerSearchItems = viewModel.customerSearch.result.collectAsLazyPagingItems()

    /// Related to product search
    val showProductSearchBar = viewModel.showProductSearchBar.collectAsState()
    val productQuery = viewModel.productSearch.query.collectAsState()
    val productSearchItems = viewModel.productSearch.result.collectAsLazyPagingItems()

    /// Input state related
    val inputState = viewModel.inputState.collectAsState()

    /// UiState related
    val uiState = viewModel.uiState.collectAsState()

    Box {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.invoice_create)) },
                    navigationIcon = {
                        IconButton(onClick = onRequestDismiss) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        DraftIndicator(
                            loading = uiState.value.isSavingDraft,
                        )
                        Button(
                            // TODO: fix this
                            enabled = false,
                            onClick = {},
                        ) { Text(stringResource(R.string.save)) }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                SecondaryTabRow(
                    selectedTabIndex = selectedDestination
                ) {
                    CreateInvoiceDestination.entries.forEachIndexed { index, destination ->
                        Tab(
                            selected = selectedDestination == index,
                            onClick = {
                                // FIXME: I don't know if this is correct
                                //  I have searched the interwebs for information about how to clear
                                //  _completely_ the back stack, and nothing seems to work as I
                                //  expect it to. This most likely means I'm not doing something
                                //  correctly, but I seem unable to find more information that
                                //  explains otherwise.
                                navHostController.popBackStack(0, false)

                                navHostController.navigate(route = destination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                selectedDestination = index
                            },
                            text = { Text(stringResource(destination.labelStringResource)) },
                        )
                    }
                }
                NavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    navController = navHostController,
                    startDestination = CreateInvoiceDestination.DETAILS.route,
                ) {
                    composable(CreateInvoiceDestination.DETAILS.route) {
                        CreateInvoiceDetailsForm(
                            customerInput = inputState.value.customer,
                            onOpenSearchCustomer = { viewModel.openCustomerSearchBar() },
                        )
                    }

                    composable(CreateInvoiceDestination.PRODUCTS.route) {
                        CreateInvoiceProductsForm(
                            invoiceProducts = inputState.value.invoiceProducts,
                            totalValue = uiState.value.totalValue,
                            onOpenSearchProduct = { viewModel.openProductSearchBar() },
                            onEditInvoiceProduct = { viewModel.openAddInvoiceProductDialog(it) },
                            onDeleteInvoiceProduct = { viewModel.deleteInvoiceProduct(it) },
                        )
                    }
                }
            }
        }

        /// Search customer related
        AnimatedVisibility(
            visible = showCustomerSearchBar.value,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<CustomerFilterDto>(
                query = customerQuery.value,
                onQueryChange = { viewModel.customerSearch.updateQuery(it) },
                expanded = showCustomerSearchBar.value,
                onExpandedChange = {
                    if (it) viewModel.openCustomerSearchBar() else viewModel.closeCustomerSearchBar()
                },
                onSearch = {
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    customerSearchItems
                        .takeIf { it.itemCount > 0 }
                        ?.peek(0)
                        ?.let { viewModel.updateCustomer(it) }
                },
                lazyPagingItems = customerSearchItems,
            ) {
                CustomerFilterListItem(dto = it, onClick = { viewModel.updateCustomer(it) })
            }
        }

        /// Search product related
        AnimatedVisibility(
            visible = showProductSearchBar.value,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<ProductFilterDto>(
                query = productQuery.value,
                onQueryChange = { viewModel.productSearch.updateQuery(it) },
                expanded = true,
                onExpandedChange = {
                    if (it) viewModel.openProductSearchBar() else viewModel.closeProductSearchBar()
                },
                onSearch = {
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    productSearchItems
                        .takeIf { it.itemCount > 0 }
                        ?.peek(0)
                        ?.let { viewModel.openAddInvoiceProductDialog(it) }
                },
                lazyPagingItems = productSearchItems,
            ) {
                ProductFilterListItem(
                    dto = it,
                    onClick = { viewModel.openAddInvoiceProductDialog(it) },
                )
            }
        }
    }

    /// Dialogs related
    AnimatedVisibility(
        visible = uiState.value.isLoading,
    ) {
        LoadingDialog(
            headlineStringResource = R.string.invoice_loading_draft_dialog_headline,
        )
    }

    if (uiState.value.isShowingAddInvoiceProductDialog) {
        AddInvoiceProductDialog(
            name = inputState.value.currentInvoiceProductInput.name!!,
            price = inputState.value.currentInvoiceProductInput.price,
            onChangePrice = { viewModel.updateCurrentInvoiceProductPrice(it) },
            count = inputState.value.currentInvoiceProductInput.count,
            onChangeCount = { viewModel.updateCurrentInvoiceProductCount(it) },
            onDismissRequest = { viewModel.closeAddInvoiceProductDialog() },
            onAddRequest = { viewModel.addCurrentInvoiceProduct() },
        )
    }
}

/// Private components

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddInvoiceProductDialog(
    name: String,
    price: Input<String>,
    onChangePrice: (String?) -> Unit,
    count: Input<String>,
    onChangeCount: (String?) -> Unit,
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
                        visualTransformation = CurrencyVisualTransformation(),
                        onValueChange = { onChangePrice(it) },
                        onClear = { onChangePrice(null) },
                        errors = price.errors.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    // Stock
                    CustomTextField(
                        label = R.string.amount,
                        value = count.value,
                        onValueChange = { onChangeCount(it) },
                        onClear = { onChangeCount(null) },
                        errors = count.errors.map { it.getStringResource() },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
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
                    Button(onClick = { onAddRequest() }) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}
