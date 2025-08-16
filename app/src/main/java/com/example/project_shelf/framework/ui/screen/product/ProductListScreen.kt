package com.example.project_shelf.framework.ui.screen.product

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.common.DeletionExtension
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.product.ProductRouteViewModel
import com.example.project_shelf.common.Id
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchExtension
import com.example.project_shelf.framework.ui.components.list_item.ProductFilterListItem
import com.example.project_shelf.framework.ui.components.list_item.ProductListItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, FlowPreview::class)
@Composable
fun ProductListScreen(
    productList: Flow<PagingData<ProductDto>>,
    callback: ProductRouteViewModel.Callback,
    // Search related
    searchState: SearchExtension.State,
    searchCallback: SearchExtension.Callback,
    searchResult: Flow<PagingData<ProductFilterDto>>,
    // Deletion related
    deletionState: DeletionExtension.State<Id>,
    deletionCallback: DeletionExtension.Callback<Id>,
) {
    /// Related to UI behavior.
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    /// Related to listing the items.
    val items = productList.collectAsLazyPagingItems()

    LaunchedEffect(items.itemCount) {
        Log.d("test", items.itemCount.toString())
    }

    /// Related to deletion snackbar.
    val snackbarState = SnackbarHostState()
    LaunchedEffect(deletionState.itemsPendingForDeletion) {
        deletionState.itemsPendingForDeletion.forEach {
            val result = snackbarState.showSnackbar(
                message = context.getString(R.string.product_deleted),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Long,
                withDismissAction = true,
            )

            when (result) {
                SnackbarResult.ActionPerformed -> {
                    deletionCallback.onUnsetItemPendingForDeletion(it)
                }

                SnackbarResult.Dismissed -> {
                    deletionCallback.onDismissItemPendingForDeletion(it)
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
                    title = { Text(stringResource(R.string.products)) },
                    actions = {
                        IconButton(onClick = { searchCallback.onOpenSearch() }) {
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
                    visible = !searchState.isSearchOpen,
                    enter = slideInHorizontally(initialOffsetX = { it * 2 }),
                    exit = slideOutHorizontally(targetOffsetX = { it * 2 }),
                ) {
                    // https://m3.material.io/components/floating-action-button/specs#9b01cb13-7a33-41c9-ab18-56443472d7e9
                    FloatingActionButton(
                        modifier = Modifier.size(80.dp),
                        onClick = { callback.onRequestCreateProduct() },
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                CustomList(
                    lazyPagingItems = items,
                    lazyListState = rememberLazyListState(),
                    emptyMessage = stringResource(R.string.products_none),
                ) {
                    ProductListItem(
                        dto = it,
                        onClick = { callback.onRequestOpenProduct(it.id) },
                    )
                }
            }
        }

        // Search Product Search Bar.
        CustomSearchExtension(
            result = searchResult, state = searchState, callback = searchCallback,
            onSearch = {
                callback.onRequestOpenProduct(it.id)
                searchCallback.onCloseSearch()
            },
        ) {
            ProductFilterListItem(
                dto = it,
                onClick = {
                    callback.onRequestOpenProduct(it.id)
                    searchCallback.onCloseSearch()
                },
            )
        }
    }
}
