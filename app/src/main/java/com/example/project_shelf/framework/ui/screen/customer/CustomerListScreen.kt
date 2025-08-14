package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.customer.CustomerListScreenViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.CustomerFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.CustomerListItem
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    state: CustomerListScreenViewModel.State,
    callback: CustomerListScreenViewModel.Callback,
    searchState: SearchExtension.State,
    searchCallback: SearchExtension.Callback,
    searchResult: Flow<PagingData<CustomerFilterDto>>,
) {
    val context = LocalContext.current

    /// Related to UI behavior.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    /// Related to search
    val searchItems = searchResult.collectAsLazyPagingItems()

    /// Related to deletion snackbar.
    val snackbarState = SnackbarHostState()
    LaunchedEffect(state.customersMarkedForDeletion) {
        state.customersMarkedForDeletion.forEach {
            val result = snackbarState.showSnackbar(
                message = context.getString(R.string.customer_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Long,
                withDismissAction = true,
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    callback.onUnsetCustomerPendingForDeletion(it)
                }

                SnackbarResult.Dismissed -> {
                    callback.onDismissCustomerMarkedForDeletion(it)
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
                        IconButton(onClick = { callback.onRequestCreateCustomer }) {
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
                    visible = !state.isShowingSearch,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally(),
                ) {
                    // https://m3.material.io/components/floating-action-button/specs#0a064a5d-8373-4150-9665-40acd0f14b0a
                    FloatingActionButton(
                        modifier = Modifier.size(96.dp),
                        onClick = { callback.onRequestCreateCustomer() },
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
                    lazyPagingItems = searchItems,
                    lazyListState = rememberLazyListState(),
                    emptyMessage = stringResource(R.string.customers_none),
                ) {
                    Text(it.name)
                }
            }

            /// Search bar
            AnimatedVisibility(
                visible = state.isShowingSearch,
                enter = slideInVertically(initialOffsetY = { -it * 2 }),
                exit = slideOutVertically(targetOffsetY = { -it * 2 })
            ) {
                CustomSearchBar<CustomerFilterDto>(
                    query = searchState.query,
                    onQueryChange = { searchCallback.onUpdateQuery(it) },
                    expanded = state.isShowingSearch,
                    onExpandedChange = { if (it) callback.onOpenSearch() else callback.onCloseSearch },
                    onSearch = {
                        callback.onCloseSearch()

                        // If the user presses the search button, without selecting an item, we
                        // will assume it wanted to select the first-most item in the search
                        // list, if there was one.
                        searchItems.takeIf { it.itemCount > 0 }
                            ?.peek(0)
                            ?.let { callback.onRequestEditCustomer(it.id) }
                    },
                    lazyPagingItems = searchItems,
                ) {
                    CustomerFilterListItem(
                        dto = it,
                        onClick = {
                            callback.onCloseSearch()
                            callback.onRequestEditCustomer(it.id)
                        },
                    )
                }
            }
        }
    }
}