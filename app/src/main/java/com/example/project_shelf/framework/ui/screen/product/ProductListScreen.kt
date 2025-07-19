package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.ProductListViewModel
import com.example.project_shelf.adapter.view_model.product.ProductSearchViewModel
import com.example.project_shelf.framework.ui.components.ProductList
import com.example.project_shelf.framework.ui.util.CollapsingAppBarNestedScrollConnection
import kotlinx.coroutines.FlowPreview

val AppBarHeight = 72.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class, FlowPreview::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    productDeletionViewModel: ProductDeletionViewModel,
    searchViewModel: ProductSearchViewModel = hiltViewModel(),
    onProductCreate: () -> Unit,
    onProductEdit: (product: ProductDto) -> Unit,
) {
    val lazyPagingItems = viewModel.products.collectAsLazyPagingItems()
    val lazyPagingSearchItems = searchViewModel.result.collectAsLazyPagingItems()
    val query = searchViewModel.query.collectAsState()

    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current
    var showSearchBar by remember { mutableStateOf(false) }

    var showTools: Boolean by remember { mutableStateOf(true) }
    val snackbarState = productDeletionViewModel.snackbarState.collectAsState()

    // Start the undo deletion snackbar. The snackbar state might recreate, when the user wants to
    // edit, or create an object. As such, we have to be aware of this.
    LaunchedEffect(snackbarState.value) {
        productDeletionViewModel.startSnackbar(
            localContext.getString(R.string.product_deleted),
            localContext.getString(R.string.undo),
        )
    }

    // Nested scrolling for top bars or any other bars.
    // https://medium.com/androiddevelopers/understanding-nested-scrolling-in-jetpack-compose-eb57c1ea0af0
    val appBarMaxHeight = with(LocalDensity.current) { AppBarHeight.roundToPx() }
    val connection = remember(appBarMaxHeight) {
        CollapsingAppBarNestedScrollConnection(appBarMaxHeight)
    }
    val density = LocalDensity.current
    val spaceHeight by remember(density) {
        derivedStateOf {
            with(density) {
                (appBarMaxHeight + connection.appBarOffset).toDp()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarState.value) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showTools && !showSearchBar,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.height(56.dp),
                    onClick = onProductCreate,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        contentDescription = null,
                        imageVector = Icons.Filled.Add,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column {
                Spacer(
                    modifier = Modifier
                        .height(spaceHeight)
                )
                ProductList(
                    lazyListState = viewModel.lazyListState,
                    lazyPagingItems = lazyPagingItems,
                    onProductClicked = onProductEdit,
                    nestedScrollConnection = connection,
                )
            }

            SearchBar(
                // https://medium.com/androiddevelopers/understanding-nested-scrolling-in-jetpack-compose-eb57c1ea0af0
                modifier = Modifier.offset { IntOffset(0, connection.appBarOffset) },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query.value,
                        onQueryChange = { searchViewModel.updateQuery(it) },
                        onSearch = { showSearchBar = false },
                        expanded = showSearchBar,
                        onExpandedChange = { showSearchBar = it },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.magnifying_glass_solid),
                                contentDescription = null,
                            )
                        },
                        placeholder = { Text(stringResource(R.string.search)) },
                    )
                },
                expanded = showSearchBar,
                onExpandedChange = { showSearchBar = it },
            ) {
                LazyColumn {
                    items(count = lazyPagingSearchItems.itemCount) { index ->
                        lazyPagingSearchItems[index]?.let {
                            Text(it.name)
                        }

                        if (index < lazyPagingItems.itemCount - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
