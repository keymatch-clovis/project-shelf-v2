package com.example.project_shelf.framework.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.example.project_shelf.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    lazyPagingItems: LazyPagingItems<T>,
    renderer: @Composable (T) -> Unit,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text(stringResource(R.string.search)) },
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
            )
        },
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange(it)
        },
    ) {
        LazyColumn {
            items(count = lazyPagingItems.itemCount) { index ->
                lazyPagingItems[index]?.let {
                    renderer(it)
                }

                if (index < lazyPagingItems.itemCount - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}