package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.common.extension.currencyUnitFromDefaultLocale
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceProductDialogViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceScreenViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDetailsFormViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceProductListFormViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.DraftIndicator
import com.example.project_shelf.framework.ui.components.dialog.LoadingDialog
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.ProductFilterListItem
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource
import com.example.project_shelf.framework.ui.common.CurrencyVisualTransformation
import com.example.project_shelf.framework.ui.common.extension.toFormattedString
import com.example.project_shelf.framework.ui.components.dialog.CustomDialog
import kotlinx.coroutines.flow.Flow
import org.joda.money.Money

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    draftViewModel: InvoiceDraftViewModel,
    state: CreateInvoiceScreenViewModel.State,
    callback: CreateInvoiceScreenViewModel.Callback,
    // Related to customer search
    customerSearchState: SearchExtension.State,
    customerSearchCallback: SearchExtension.Callback,
    customerSearchResult: Flow<PagingData<CustomerFilterDto>>,
    // Related to product search
    productSearchState: SearchExtension.State,
    productSearchCallback: SearchExtension.Callback,
    productSearchResult: Flow<PagingData<ProductFilterDto>>,
    // Related to filling the invoice details.
    invoiceDetailsFormState: InvoiceDetailsFormViewModel.State,
    invoiceDetailsFormCallback: InvoiceDetailsFormViewModel.Callback,
    // Related to listing the products added to the invoice.
    invoiceProductListFormState: InvoiceProductListFormViewModel.State,
    invoiceProductListFormCallback: InvoiceProductListFormViewModel.Callback,
    // Related to adding products to the invoice.
    createInvoiceProductDialogState: CreateInvoiceProductDialogViewModel.State,
    createInvoiceProductDialogCallback: CreateInvoiceProductDialogViewModel.Callback,
) {
    /// Navigation related
    val navHostController = rememberNavController()
    val startDestination = CreateInvoiceDestination.DETAILS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    /// Search related
    val customerSearchItems = customerSearchResult.collectAsLazyPagingItems()
    val productSearchItems = productSearchResult.collectAsLazyPagingItems()

    Box {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.invoice_create)) },
                    navigationIcon = {
                        IconButton(onClick = { callback.onCloseRequest() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        DraftIndicator(loading = state.isSavingDraft)
                        Button(
                            enabled = state.isValid,
                            onClick = { callback.onCreateRequest() },
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
                        InvoiceDetailsForm(
                            state = invoiceDetailsFormState,
                            callback = invoiceDetailsFormCallback,
                        )
                    }

                    composable(CreateInvoiceDestination.PRODUCTS.route) {
                        InvoiceProductListForm(
                            state = invoiceProductListFormState,
                            callback = invoiceProductListFormCallback,
                            createInvoiceProductDialogState = createInvoiceProductDialogState,
                            createInvoiceProductDialogCallback = createInvoiceProductDialogCallback,
                        )
                    }
                }
            }
        }

        /// Search customer related
        AnimatedVisibility(
            visible = state.isShowingCustomerSearch,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<CustomerFilterDto>(
                query = customerSearchState.query,
                onQueryChange = { customerSearchCallback.onUpdateQuery(it) },
                expanded = state.isShowingCustomerSearch,
                onExpandedChange = { if (it) callback.onOpenCustomerSearch() else callback.onCloseCustomerSearch() },
                onSearch = {
                    callback.onCloseCustomerSearch()

                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    customerSearchItems.takeIf { it.itemCount > 0 }
                        ?.peek(0)
                        ?.let { invoiceDetailsFormCallback.onSetCustomer(it) }
                },
                lazyPagingItems = customerSearchItems,
            ) {
                CustomerFilterListItem(
                    dto = it,
                    onClick = {
                        callback.onCloseCustomerSearch()
                        invoiceDetailsFormCallback.onSetCustomer(it)
                    },
                )
            }
        }

        /// Search product related
        AnimatedVisibility(
            visible = state.isShowingProductSearch,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<ProductFilterDto>(
                query = productSearchState.query,
                onQueryChange = { productSearchCallback.onUpdateQuery(it) },
                expanded = true,
                onExpandedChange = { if (it) callback.onOpenProductSearch() else callback.onCloseProductSearch() },
                onSearch = {
                    callback.onCloseProductSearch()
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    productSearchItems.takeIf { it.itemCount > 0 }
                        ?.peek(0)
                        ?.let {
                            invoiceProductListFormCallback.onAddRequest(
                                InvoiceProductDto(
                                    productId = it.id,
                                    name = it.name,
                                    // TODO: Fix this.
                                    price = Money.zero(currencyUnitFromDefaultLocale()),
                                    count = 0,
                                )
                            )
                        }
                },
                lazyPagingItems = productSearchItems,
            ) {
                ProductFilterListItem(
                    dto = it,
                    onClick = {
                        callback.onCloseProductSearch()
                        invoiceProductListFormCallback.onAddRequest(
                            InvoiceProductDto(
                                productId = it.id,
                                name = it.name,
                                // TODO: Fix this.
                                price = Money.zero(currencyUnitFromDefaultLocale()),
                                count = 0,
                            )
                        )
                    },
                )
            }
        }
    }

    /// Dialogs related
    if (state.isLoadingDraft) {
        LoadingDialog(
            headlineStringResource = R.string.invoice_loading_draft_dialog_headline,
        )
    }
}

/// Private components
@Composable
private fun InvoiceDetailsForm(
    state: InvoiceDetailsFormViewModel.State,
    callback: InvoiceDetailsFormViewModel.Callback,
) {
    Column(
        // https://m3.material.io/components/dialogs/specs#2b93ced7-9b0d-4a59-9bc4-8ff59dcd24c1
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        /// Number
        CustomTextField(
            label = R.string.number,
            readOnly = true,
            required = true,
            value = state.number,
        )
        Spacer(modifier = Modifier.height(16.dp))
        /// Customer
        CustomTextField(
            required = true,
            label = R.string.customer,
            readOnly = true,
            value = state.customer.value?.name ?: "",
            errors = state.customer.errors.map { it.getStringResource() },
            onClick = { callback.onOpenCustomerSearch() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceProductListForm(
    state: InvoiceProductListFormViewModel.State,
    callback: InvoiceProductListFormViewModel.Callback,

    createInvoiceProductDialogState: CreateInvoiceProductDialogViewModel.State,
    createInvoiceProductDialogCallback: CreateInvoiceProductDialogViewModel.Callback,
) {
    /// Related to UI behavior.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Button(
                        onClick = { callback.onOpenSearchProduct() },
                    ) {
                        Icon(
                            // https://m3.material.io/components/split-button/specs
                            modifier = Modifier.size(20.dp),
                            contentDescription = null,
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                        )
                        Text(stringResource(R.string.product_add))
                    }
                },
            )
        },
        bottomBar = {
            HorizontalDivider()
            Column(modifier = Modifier.padding(8.dp)) {
                Row {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = "${stringResource(R.string.total).uppercase()}:",
                    )
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = state.totalValue.toFormattedString(withSymbol = true)
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            /// Empty products label
            if (state.invoiceProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            imageVector = ImageVector.vectorResource(R.drawable.package_open),
                            contentDescription = null,
                        )
                        Text(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            text = stringResource(R.string.products_none),
                        )
                    }
                }
            }

            // TODO: Convert this to a lazy column.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .verticalScroll(rememberScrollState()),
            ) {
                state.invoiceProducts.forEachIndexed { index, item ->
                    ListItem(
                        modifier = Modifier.fillMaxWidth(),
                        headlineContent = {
                            Text(
                                style = MaterialTheme.typography.titleLarge,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                text = item.name,
                            )
                        },
                        supportingContent = {
                            Column {
                                Text(item.price.toFormattedString(withSymbol = true))
                                Text(item.count.let { if (it > 9999) "+9999" else it.toString() })
                            }
                        },
                        trailingContent = {
                            InvoiceProductDropdownMenu(
                                onEditRequest = { callback.onEditRequest(item, index) },
                                onDeleteRequest = { callback.onRemoveRequest(item) },
                            )
                        },
                    )

                    if (index < state.invoiceProducts.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    // NOTE: We could leave this dialog directly in the screen. That way we wouldn't need to drill
    //  these properties here. But, I think this is better, as the dialog is more relevant in the
    //  list of invoices, and not in the main screen. But maybe we'll change our minds in the
    //  future.
    if (state.isShowingAddProductDialog) {
        CreateInvoiceProductDialog(
            state = createInvoiceProductDialogState,
            callback = createInvoiceProductDialogCallback,
        )
    }
}

@Composable
private fun InvoiceProductDropdownMenu(
    onEditRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    // https://developer.android.com/develop/ui/compose/components/menu
    Box {
        IconButton(
            onClick = { expanded = !expanded }) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ellipsis_vertical),
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onEditRequest()
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.pencil),
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(R.string.edit)) },
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onDeleteRequest()
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.trash),
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(R.string.delete)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateInvoiceProductDialog(
    state: CreateInvoiceProductDialogViewModel.State,
    callback: CreateInvoiceProductDialogViewModel.Callback,
) {
    CustomDialog(
        onDismissRequest = callback.onDismissRequest,
    ) {
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
                value = state.name,
            )
            CustomTextField(
                label = R.string.price,
                value = state.price.value ?: "",
                visualTransformation = CurrencyVisualTransformation(),
                onValueChange = { callback.onPriceChange(it) },
                onClear = { callback.onPriceChange("") },
                errors = state.price.errors.map { it.getStringResource() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
            )
            // Stock
            CustomTextField(
                label = R.string.amount,
                value = state.count.value ?: "",
                onValueChange = { callback.onCountChange(it) },
                onClear = { callback.onCountChange("") },
                errors = state.count.errors.map { it.getStringResource() },
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
            TextButton(onClick = { callback.onDismissRequest() }) {
                Text(stringResource(R.string.cancel))
            }
            Button(onClick = { callback.onCreateRequest(state) }) {
                Text(stringResource(R.string.add))
            }
        }
    }
}
