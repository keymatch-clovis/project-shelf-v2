package com.example.project_shelf.framework.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.project_shelf.R

@Composable
fun <T : Any> CustomList(
    lazyListState: LazyListState,
    lazyPagingItems: LazyPagingItems<T>,
    nestedScrollConnection: NestedScrollConnection,
    emptyMessage: String,
    renderer: @Composable (T) -> Unit,
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
                    imageVector = ImageVector.vectorResource(R.drawable.box_open_solid),
                    contentDescription = null,
                )
                Text(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    text = emptyMessage,
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection),
        ) {
            items(count = lazyPagingItems.itemCount) { index ->
                lazyPagingItems[index]?.let {
                    renderer(it)
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

        AnimatedVisibility(
            visible = lazyPagingItems.loadState.refresh == LoadState.Loading,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    shape = AbsoluteRoundedCornerShape(100),
                    // https://m3.material.io/styles/elevation/tokens
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}