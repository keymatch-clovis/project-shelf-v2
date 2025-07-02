package com.example.project_shelf.framework.ui.screen.product

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalMaterial3Api::class)
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
    val isVisible = rememberSaveable { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
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
            lazyPagingItems,
            lazyListState = viewModel.lazyListState,
            onProductClicked = onProductEdit,
        )
    }
}