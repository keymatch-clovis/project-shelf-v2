package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.InvoiceFilterDto
import com.example.project_shelf.adapter.view_model.invoice.InvoiceListViewModel
import com.example.project_shelf.framework.ui.components.CustomList
import com.example.project_shelf.framework.ui.components.CustomSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onRequestEdit: (invoiceId: Long) -> Unit,
    onRequestCreate: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    /// Related to invoice listing.
    val lazyPagingItems = viewModel.invoices.collectAsLazyPagingItems()

    /// Related to invoice search
    var showSearchBar = viewModel.showSearchBar.collectAsState()
    var query = viewModel.query.collectAsState()
    val lazyPagingSearchItems = viewModel.searchResult.collectAsLazyPagingItems()

    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(R.string.invoices)) },
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
        }, floatingActionButton = {
            AnimatedVisibility(
                visible = !showSearchBar.value,
                enter = slideInVertically(),
                exit = slideOutVertically(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.height(56.dp),
                    onClick = { onRequestCreate() },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(R.drawable.plus),
                    )
                }
            }
        }) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.TopCenter,
            ) {
                CustomList(
                    lazyPagingItems = lazyPagingItems,
                    lazyListState = viewModel.lazyListState,
                    emptyMessage = stringResource(R.string.invoices_none),
                ) {
                    Text(it.customer.name)
                }
            }

            AnimatedVisibility(
                visible = showSearchBar.value,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                CustomSearchBar<InvoiceFilterDto>(
                    query = query.value,
                    onQueryChange = { viewModel.updateQuery(it) },
                    expanded = showSearchBar.value,
                    onExpandedChange = { if (it) viewModel.openSearchBar() else viewModel.closeSearchBar() },
                    onSearch = {
                        // If the user presses the search button, without selecting an item, we
                        // will assume it wanted to select the first-most item in the search
                        // list, if there was one.
                        lazyPagingSearchItems.peek(0)?.let { onRequestEdit(it.id) }
                        viewModel.closeSearchBar()
                    },
                    lazyPagingItems = lazyPagingSearchItems,
                ) {
                    Text(it.number.toString())
                }
            }
        }
    }
}