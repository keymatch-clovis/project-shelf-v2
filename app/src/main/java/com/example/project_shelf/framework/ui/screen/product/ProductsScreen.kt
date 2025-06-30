package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.ProductUiState
import com.example.project_shelf.adapter.view_model.ProductsViewModel
import com.example.project_shelf.framework.ui.components.ProductList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductCreate: () -> Unit,
    onProductEdit: (product: ProductUiState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lazyPagingItems = viewModel.products.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }
    val isVisible = rememberSaveable { mutableStateOf(true) }
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    isVisible.value = false
                }

                if (available.y > 1) {
                    isVisible.value = true
                }

                return super.onPreScroll(available, source)
            }
        }
    }

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            searchBarState = searchBarState,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            textFieldState = textFieldState,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            placeholder = { Text("Search") },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            AnimatedVisibility(
                visible = isVisible.value,
                enter = slideInVertically(initialOffsetY = { -it * 2 }),
                exit = slideOutVertically(targetOffsetY = { -it * 2 }),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TopSearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        state = searchBarState,
                        inputField = inputField,
                    )
                }
            }
            ExpandedFullScreenSearchBar(
                state = searchBarState, inputField = inputField
            ) {
                LazyColumn {
                    items(20) {
                        Text("tester")
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible.value,
                enter = slideInHorizontally(initialOffsetX = { it * 2 }),
                exit = slideOutHorizontally(targetOffsetX = { it * 2 }),
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
        ProductList(
            innerPadding,
            nestedScrollConnection,
            lazyPagingItems,
            lazyListState = viewModel.lazyListState,
            onProductClicked = onProductEdit,
        )
    }
}