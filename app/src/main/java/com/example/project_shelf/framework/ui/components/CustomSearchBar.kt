package com.example.project_shelf.framework.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    sharedTransitionScope: SharedTransitionScope,
    renderer: @Composable (T) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier.align(Alignment.TopCenter),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.focusRequester(focusRequester),
                        expanded = expanded,
                        onExpandedChange = onExpandedChange,
                        placeholder = { Text(stringResource(R.string.search)) },
                        leadingIcon = {
                            IconButton(
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.arrow_left_solid),
                                    contentDescription = null,
                                )
                            }
                        },
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
    }
}