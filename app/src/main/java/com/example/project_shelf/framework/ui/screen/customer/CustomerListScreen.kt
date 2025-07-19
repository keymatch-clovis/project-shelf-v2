package com.example.project_shelf.framework.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerDto
import com.example.project_shelf.adapter.view_model.customer.CustomerDeletionViewModel
import com.example.project_shelf.adapter.view_model.customer.CustomerSearchViewModel
import com.example.project_shelf.adapter.view_model.customer.CustomersViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.CustomerListItem
import com.example.project_shelf.framework.ui.util.customNestedScrollConnection

@Composable
fun CustomersScreen(
    viewModel: CustomersViewModel,
    deletionViewModel: CustomerDeletionViewModel,
    searchViewModel: CustomerSearchViewModel,
    onCreateRequest: () -> Unit,
    onEditRequest: (dto: CustomerDto) -> Unit,
) {
    val localContext = LocalContext.current

    // Start the undo deletion snackbar. The snackbar state might recreate, when the user wants to
    // edit, or create an object. As such, we have to be aware of this.
    val snackbarState = deletionViewModel.snackbarState.collectAsState()
    LaunchedEffect(snackbarState.value) {
        deletionViewModel.startSnackbar(
            localContext.getString(R.string.customer_deleted),
            localContext.getString(R.string.undo),
        )
    }

    // Show window tools based on the current scroll.
    val showTools = remember { mutableStateOf(true) }
    val nestedScrollConnection = customNestedScrollConnection(showTools)

    // Related to search.
    val query = searchViewModel.query.collectAsState()
    var showSearchBar by remember { mutableStateOf(false) }
    val lazyPagingSearchItems = searchViewModel.result.collectAsLazyPagingItems()
    val padding: Dp by animateDpAsState(
        if (showTools.value) {
            // We have to manually guess this is the correct size to add the padding on an element.
            // https://m3.material.io/components/app-bars/specs#14978a2b-e102-46df-8103-c0365076be82
            72.dp
        } else {
            0.dp
        }
    )

    // Related to this customer list.
    val lazyPagingItems = viewModel.customers.collectAsLazyPagingItems()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarState.value) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showTools.value && !showSearchBar,
            ) {
                FloatingActionButton(
                    modifier = Modifier.height(56.dp),
                    onClick = onCreateRequest,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(R.drawable.plus),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    // Add or remove the top padding if the search bar is being shown.
                    .padding(top = padding)
            ) {
                CustomList(
                    lazyPagingItems = lazyPagingItems,
                    lazyListState = viewModel.lazyListState,
                    nestedScrollConnection = nestedScrollConnection,
                    emptyMessage = stringResource(R.string.customers_none),
                ) {
                    CustomerListItem(dto = it, onClick = {})
                }
            }

            AnimatedVisibility(
                visible = showTools.value,
                enter = slideInVertically(initialOffsetY = { -it * 2 }),
                exit = slideOutVertically(targetOffsetY = { -it * 2 })
            ) {
                CustomSearchBar<CustomerDto>(
                    query = query.value,
                    onQueryChange = { searchViewModel.updateQuery(it) },
                    expanded = showSearchBar,
                    onExpandedChange = { showSearchBar = it },
                    onSearch = { showSearchBar = false },
                    lazyPagingItems = lazyPagingSearchItems,
                ) { Text(it.name) }
            }
        }
    }
}