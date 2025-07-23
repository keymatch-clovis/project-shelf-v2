package com.example.project_shelf.framework.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.project_shelf.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
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
    // Always request focus when the custom search bar is opened.
    // NOTE:
    //  This can be a problem if the custom search bar
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.captureFocus()
        focusRequester.requestFocus()
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = null,
                    )
                },
                placeholder = { Text(stringResource(R.string.search)) },
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
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