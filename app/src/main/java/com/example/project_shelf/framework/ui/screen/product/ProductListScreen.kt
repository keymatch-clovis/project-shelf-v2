package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.ProductListViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.ProductList
import com.example.project_shelf.framework.ui.components.list_item.ProductListItem
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, FlowPreview::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    productDeletionViewModel: ProductDeletionViewModel,
    onProductCreate: () -> Unit,
    onProductEdit: (product: ProductDto) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val lazyPagingItems = viewModel.products.collectAsLazyPagingItems()

    val localContext = LocalContext.current

    val snackbarState = productDeletionViewModel.snackbarState.collectAsState()

    // Related to product search
    var showSearchBar = viewModel.showSearchBar.collectAsState()
    var query = viewModel.query.collectAsState()
    val lazyPagingSearchItems = viewModel.searchResult.collectAsLazyPagingItems()

    // Start the undo deletion snackbar. The snackbar state might recreate, when the user wants to
    // edit, or create an object. As such, we have to be aware of this.
    LaunchedEffect(snackbarState.value) {
        productDeletionViewModel.startSnackbar(
            localContext.getString(R.string.product_deleted),
            localContext.getString(R.string.undo),
        )
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
                    FloatingActionButton(
                        modifier = Modifier.height(56.dp),
                        onClick = onProductCreate,
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
            ProductList(
                modifier = Modifier.padding(innerPadding),
                lazyListState = viewModel.lazyListState,
                lazyPagingItems = lazyPagingItems,
                onProductClicked = onProductEdit,
            )
        }

        // Search Product Search Bar.
        AnimatedVisibility(
            visible = showSearchBar.value,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
        ) {
            val focusRequester = remember { FocusRequester() }
            // NOTE:
            //  Idk if this is working correctly, but I guess I have no other way of knowing when
            //  this element is already in the composition tree.
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                focusRequester.captureFocus()
            }

            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.focusRequester(focusRequester),
                        query = query.value,
                        onSearch = {
                            // If the user presses the search button, without selecting an item, we
                            // will assume it wanted to select the first-most item in the search
                            // list, if there was one.
                            lazyPagingSearchItems.peek(0)?.let { onProductEdit(it) }
                            viewModel.closeSearchBar()
                        },
                        onQueryChange = { viewModel.updateQuery(it) },
                        expanded = showSearchBar.value,
                        onExpandedChange = { if (it) viewModel.openSearchBar() else viewModel.closeSearchBar() },
                        leadingIcon = {
                            IconButton(onClick = { viewModel.closeSearchBar() }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                                    contentDescription = null,
                                )
                            }
                        },
                        placeholder = { Text(stringResource(R.string.search)) },
                    )
                },
                expanded = showSearchBar.value,
                onExpandedChange = { if (it) viewModel.openSearchBar() else viewModel.closeSearchBar() },
            ) {
                CustomList(
                    lazyPagingItems = lazyPagingSearchItems,
                    lazyListState = viewModel.lazyListState,
                    emptyMessage = stringResource(R.string.products_none),
                ) {
                    ProductListItem(dto = it, onClick = {
                        onProductEdit(it)
                        viewModel.closeSearchBar()
                    })
                }
            }
        }
    }
}
