package com.example.project_shelf.framework.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> CustomSearchExtension(
    result: Flow<PagingData<T>>,
    state: SearchExtension.State,
    callback: SearchExtension.Callback,
    onSearch: (T) -> Unit,
    component: @Composable (T) -> Unit,
) {
    val items = result.collectAsLazyPagingItems()

    AnimatedVisibility(
        visible = state.isSearchOpen,
        enter = slideInVertically(initialOffsetY = { -it * 2 }),
        exit = slideOutVertically(targetOffsetY = { -it * 2 })
    ) {
        CustomSearchBar<T>(
            query = state.query,
            onQueryChange = { callback.onUpdateQuery(it) },
            expanded = state.isSearchOpen,
            onExpandedChange = { if (it) callback.onOpenSearch() else callback.onCloseSearch() },
            onSearch = {
                callback.onCloseSearch()
                // If the user presses the search button, without selecting an item, we
                // will assume it wanted to select the first-most item in the search
                // list, if there was one.
                items.takeIf { it.itemCount > 0 }
                    ?.peek(0)
                    ?.let { onSearch(it) }
            },
            lazyPagingItems = items,
        ) {
            component(it)
        }
    }
}