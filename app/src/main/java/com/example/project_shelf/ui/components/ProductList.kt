package com.example.project_shelf.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.project_shelf.adapter.view_model.ProductUiState
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun ProductList(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    nestedScrollConnection: NestedScrollConnection,
    lazyPagingItems: LazyPagingItems<ProductUiState>,
    lazyListState: LazyListState,
    onProductClicked: (product: ProductUiState) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxWidth(),
        contentPadding = contentPadding,
    ) {
        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
            item {
                Text("Wainting for items to load")
            }
        }

        items(count = lazyPagingItems.itemCount) { index ->
            lazyPagingItems[index]?.let {
                ProductListItem(it, onItemClicked = onProductClicked)
            }
            HorizontalDivider()
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