package com.example.project_shelf.framework.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.example.project_shelf.adapter.view_model.ProductUiState
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.project_shelf.R

@Composable
fun ProductList(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    nestedScrollConnection: NestedScrollConnection,
    lazyPagingItems: LazyPagingItems<ProductUiState>,
    lazyListState: LazyListState,
    onProductClicked: (product: ProductUiState) -> Unit,
) {
    if (lazyPagingItems.loadState.isIdle && lazyPagingItems.itemCount == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.surfaceDim,
                    imageVector = Icons.Outlined.Circle,
                    contentDescription = null,
                )
                Text(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    text = stringResource(R.string.products_none)
                )
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxWidth(),
        contentPadding = contentPadding,
    ) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        items(count = lazyPagingItems.itemCount) { index ->
            lazyPagingItems[index]?.let {
                ProductListItem(it, onItemClicked = onProductClicked)
            }

            if (index < lazyPagingItems.itemCount - 1) {
                HorizontalDivider()
            }
        }

        if (lazyPagingItems.loadState.append == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
}