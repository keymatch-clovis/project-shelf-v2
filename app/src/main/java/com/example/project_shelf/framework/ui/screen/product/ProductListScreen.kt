package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.ProductListViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.list_item.ProductFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.ProductListItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, FlowPreview::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    productDeletionViewModel: ProductDeletionViewModel,
    onRequestCreate: () -> Unit,
    onRequestEdit: (ProductDto) -> Unit,
) {
    /// Related to UI behavior.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val localContext = LocalContext.current

    /// Related to listing the items.
    val productList = viewModel.products.collectAsLazyPagingItems()

    /// Related to product search
    val showSearchBar = viewModel.showSearchBar.collectAsState()
    val query = viewModel.search.query.collectAsState()
    val searchItems = viewModel.search.result.collectAsLazyPagingItems()

    /// Related to deletion snackbar.
    // Start the undo deletion snackbar. The snackbar state might recreate, when the user wants to
    // edit, or create an object. As such, we have to be aware of this.
    val snackbarState = productDeletionViewModel.snackbarState.collectAsState()
    LaunchedEffect(snackbarState.value) {
        productDeletionViewModel.startSnackbar(
            localContext.getString(R.string.product_deleted),
            localContext.getString(R.string.undo),
        )
    }

    /// Related to event consumption
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is ProductListViewModel.Event.RequestEdit -> onRequestEdit(it.dto)
            }
        }
    }

    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackbarState.value) },
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    scrollBehavior = scrollBehavior,
                    title = { Text(stringResource(R.string.products)) },
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
                    enter = slideInVertically(),
                    exit = slideOutVertically(),
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
                    lazyPagingItems = productList,
                    lazyListState = rememberLazyListState(),
                    emptyMessage = stringResource(R.string.products_none),
                ) {
                    ProductListItem(
                        dto = it,
                        onClick = { viewModel.requestEdit(it.id) },
                    )
                }
            }
        }

        // Search Product Search Bar.
        AnimatedVisibility(
            visible = showSearchBar.value,
            enter = slideInVertically(initialOffsetY = { -it * 2 }),
            exit = slideOutVertically(targetOffsetY = { -it * 2 })
        ) {
            CustomSearchBar<ProductFilterDto>(
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
                        ?.let { viewModel.requestEdit(it.id) }
                },
                lazyPagingItems = searchItems,
            ) {
                ProductFilterListItem(
                    dto = it, onClick = { viewModel.requestEdit(it.id) })
            }
        }
    }
}
