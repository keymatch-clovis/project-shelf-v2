package com.example.project_shelf.framework.ui.screen.invoice

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.CustomerListItem
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceDestination
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceNavHost
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    viewModel: CreateInvoiceViewModel,
    onRequestDismiss: () -> Unit,
) {
    val navHostController = rememberNavController()
    val startDestination = CreateInvoiceDestination.DETAILS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    /// Related to customer search.
    val showCustomerSearchBar = viewModel.showCustomerSearchBar.collectAsState()
    val customerQuery = viewModel.search.query.collectAsState()
    val customerSearchItems = viewModel.search.result.collectAsLazyPagingItems()

    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.invoice_create)) },
                    navigationIcon = {
                        IconButton(onClick = onRequestDismiss) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.x),
                                contentDescription = null
                            )
                        }
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
                )
                HorizontalDivider()
                // https://m3.material.io/components/lists/specs
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Invoice Number: 1")
                    Text("Customer: testing")
                    Text("TOTAL: 12304")
                }
            }
        }

        /// Search customer to add them.
        AnimatedVisibility(
            visible = showCustomerSearchBar.value,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<CustomerFilterDto>(
                query = customerQuery.value,
                onQueryChange = { viewModel.search.updateQuery(it) },
                expanded = showCustomerSearchBar.value,
                onExpandedChange = {
                    Log.d("test", "$it")
                    Log.d("test", "${showCustomerSearchBar.value}")
                    if (it) {
                        viewModel.openCustomerSearchBar()
                    } else {
                        viewModel.closeCustomerSearchBar()
                    }
                },
                onSearch = {
                    // If the user presses the search button, without selecting an item, we will
                    // assume it wanted to select the first-most item in the search list, if there
                    // was one.
                    // customerSearchItems.peek(0)?.let { onProductEdit(it) }
                    viewModel.closeCustomerSearchBar()
                },
                lazyPagingItems = customerSearchItems,
            ) {
                CustomerFilterListItem(onClick = {}, dto = it)
            }
        }
    }
}