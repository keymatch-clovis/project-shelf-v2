package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.dialog.AddInvoiceProductDialog
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.ProductFilterListItem
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceDestination
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
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

    /// Related to invoice product adding
    val showAddInvoiceProductBottomSheet =
        viewModel.showAddInvoiceProductBottomSheet.collectAsState()

    /// Customer related
    val customer = viewModel.inputState.customer.rawValue.collectAsState()

    Box {
        Scaffold(
            topBar = {
                TopAppBar(
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
                                navHostController.popBackStack(
                                    navHostController.graph.startDestinationId,
                                    true,
                                )
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
                CreateInvoiceNavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    navHostController = navHostController,
                    startDestination = CreateInvoiceDestination.DETAILS,
                    emitter = viewModel.eventFlow,
                    customerInput = viewModel.inputState.customer,
                )
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
                    customerSearchItems.takeIf { it.itemCount > 0 }?.peek(0)
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
                expanded = showProductSearchBar.value,
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
                        ?.let { viewModel.addProduct(it) }
                },
                lazyPagingItems = productSearchItems,
            ) {
                ProductFilterListItem(dto = it, onClick = { viewModel.addProduct(it) })
            }
        }
    }

    /// Modals and other related components
    if (showAddInvoiceProductBottomSheet.value) {
        AddInvoiceProductDialog(
            onDismissRequest = { viewModel.closeAddInvoiceProductDialog() }
        )
    }
}