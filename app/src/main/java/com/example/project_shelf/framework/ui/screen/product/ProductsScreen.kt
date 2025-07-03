package com.example.project_shelf.framework.ui.screen.product

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.adapter.view_model.ProductSearchResultUiState
import com.example.project_shelf.adapter.view_model.ProductSearchViewModel
import kotlinx.coroutines.launch
import com.example.project_shelf.adapter.view_model.ProductUiState
import com.example.project_shelf.adapter.view_model.ProductsViewModel
import com.example.project_shelf.framework.ui.components.ProductList
import com.example.project_shelf.R
import com.example.project_shelf.framework.ui.components.CustomSearchBar
import com.example.project_shelf.framework.ui.components.SearchTopBar
import com.example.project_shelf.framework.ui.components.isLookaheadRootAvailable
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    searchViewModel: ProductSearchViewModel = hiltViewModel(),
    onProductCreate: () -> Unit,
    onProductEdit: (product: ProductUiState) -> Unit,
) {
    val lazyPagingItems = viewModel.products.collectAsLazyPagingItems()
    val searchState = searchViewModel.uiState.collectAsState()
    val lazyPagingSearchItems = searchViewModel.result.collectAsLazyPagingItems()
    val query = searchViewModel.query.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showSearchBar by remember { mutableStateOf(false) }

    var showTools: Boolean by remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    showTools = false
                }
                if (available.y > 1) {
                    showTools = true
                }
                return Offset.Zero
            }
        }
    }

    val topPadding: Dp by animateDpAsState(
        if (showTools) {
            72.dp
        } else {
            0.dp
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            Box(Modifier.padding(top = topPadding)) {
                ProductList(
                    lazyPagingItems,
                    lazyListState = viewModel.lazyListState,
                    onProductClicked = onProductEdit,
                    nestedScrollConnection = nestedScrollConnection,
                )
            }

            AnimatedVisibility(
                visible = showTools,
                enter = slideInVertically(initialOffsetY = { -it * 2 }),
                exit = slideOutVertically(targetOffsetY = { -it * 2 })
            ) {
                SearchBar(
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
}
