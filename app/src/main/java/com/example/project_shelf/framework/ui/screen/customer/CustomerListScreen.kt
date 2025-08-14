package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.view_model.customer.CustomerListViewModel
import com.example.project_shelf.adapter.view_model.customer.CustomerViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.CustomerListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    customerViewModel: CustomerViewModel,
    viewModel: CustomerListViewModel,
    onRequestCreate: () -> Unit,
    onRequestEdit: (CustomerFilterDto) -> Unit,
) {
    /// Related to customer state
    val customerState = customerViewModel.state.collectAsState()
    val context = LocalContext.current

    /// Related to UI behavior.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localContext = LocalContext.current

    /// Related to listing the items
    val customerList = viewModel.customers.collectAsLazyPagingItems()

    /// Related to search.
    var showSearchBar = viewModel.showSearchBar.collectAsState()
    val query = viewModel.search.query.collectAsState()
    val searchItems = viewModel.search.result.collectAsLazyPagingItems()

    /// Related to deletion snackbar.
    val snackbarState = SnackbarHostState()
    LaunchedEffect(customerState.value.customersMarkedForDeletion) {
        customerState.value.customersMarkedForDeletion.forEach {
            val result = snackbarState.showSnackbar(
                message = context.getString(R.string.customer_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Long,
                withDismissAction = true,
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    customerViewModel.unsetCustomerPendingForDeletion(it)
                }

                SnackbarResult.Dismissed -> {
                    customerViewModel.removeCustomerFromMarkedForDeletion(it)
                }
            }
        }
    }

    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackbarState) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    scrollBehavior = scrollBehavior,
                    title = { Text(stringResource(R.string.customers)) },
                    actions = {
                        IconButton(onClick = { viewModel.openSearchBar() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = !showSearchBar.value,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally(),
                ) {
                    // https://m3.material.io/components/floating-action-button/specs#0a064a5d-8373-4150-9665-40acd0f14b0a
                    FloatingActionButton(
                        modifier = Modifier.size(96.dp),
                        onClick = onRequestCreate,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CustomList(
                    lazyPagingItems = customerList,
                    lazyListState = viewModel.lazyListState,
                    emptyMessage = stringResource(R.string.customers_none),
                ) {
                    CustomerListItem(dto = it, onClick = {})
                }
            }

            /// Search bar
            AnimatedVisibility(
                visible = showSearchBar.value,
                enter = slideInVertically(initialOffsetY = { -it * 2 }),
                exit = slideOutVertically(targetOffsetY = { -it * 2 })
            ) {
                CustomSearchBar<CustomerFilterDto>(
                    query = query.value,
                    onQueryChange = { viewModel.search.updateQuery(it) },
                    expanded = showSearchBar.value,
                    onExpandedChange = {
                        if (it) viewModel.openSearchBar() else viewModel.closeSearchBar()
                    },
                    onSearch = {
                        // If the user presses the search button, without selecting an item, we
                        // will assume it wanted to select the first-most item in the search
                        // list, if there was one.
                        searchItems
                            .takeIf { it.itemCount > 0 }
                            ?.peek(0)
                            ?.let { onRequestEdit(it) }
                        viewModel.closeSearchBar()
                    },
                    lazyPagingItems = searchItems,
                ) {
                    CustomerFilterListItem(
                        dto = it,
                        onClick = {
                            onRequestEdit(it)
                            viewModel.closeSearchBar()
                        },
                    )
                }
            }
        }
    }
}