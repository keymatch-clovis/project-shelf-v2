package com.example.project_shelf.framework.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

// TODO: See if this is resolved later.
// https://issuetracker.google.com/issues/344343033
@OptIn(ExperimentalSharedTransitionApi::class)
val SharedTransitionScope.isLookaheadRootAvailable: Boolean
    @Composable get() {
        var isLookaheadRootAvailable by remember(this) { mutableStateOf(false) }
        return if (isLookaheadRootAvailable.not()) {
            isLookaheadRootAvailable = determineLookaheadRoot() != null
            isLookaheadRootAvailable
        } else {
            true
        }
    }

// TODO: See if this is resolved later.
// https://issuetracker.google.com/issues/344343033
@OptIn(ExperimentalSharedTransitionApi::class)
private fun SharedTransitionScope.determineLookaheadRoot(): Any? {
    return try {
        val field = this::class.java.getDeclaredField("nullableLookaheadRoot")
        field.isAccessible = true
        field.get(this)
    } catch (e: Exception) {
        Log.e(SharedTransitionScope::class.simpleName, "lookahead root not available", e)
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchTopBar(
    onClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        CenterAlignedTopAppBar(
            expandedHeight = 68.dp,
            title = {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.extraLarge,
                    // TODO: See if this is resolved later.
                    // https://issuetracker.google.com/issues/344343033
                    modifier = if (isLookaheadRootAvailable) {
                        Modifier
                            .height(56.dp)
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                rememberSharedContentState(key = "search-top-bar"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                            )
                    } else {
                        Modifier
                    },
                    onClick = onClick,
                ) {
                    // Material 3 Specs for Search Bar.
                    // https://m3.material.io/components/search/specs
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Row {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                                imageVector = ImageVector.vectorResource(R.drawable.magnifying_glass_solid),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                text = stringResource(R.string.search),
                            )
                        }
                    }
                }
//                SearchBar(
//                    // TODO: See if this is resolved later.
//                    // https://issuetracker.google.com/issues/344343033
//                    modifier = if (isLookaheadRootAvailable) {
//                        Modifier
//                            .height(60.dp)
//                            .padding(bottom = 8.dp)
//                            .sharedBounds(
//                                rememberSharedContentState(key = "search-top-bar"),
//                                animatedVisibilityScope = animatedVisibilityScope,
//                                enter = fadeIn(),
//                                exit = fadeOut(),
//                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
//                            )
//                    } else {
//                        Modifier
//                    },
//                    inputField = {
//                        SearchBarDefaults.InputField(
//                            // TODO: See if this is resolved later.
//                            // https://issuetracker.google.com/issues/344343033
//                            modifier = if (isLookaheadRootAvailable) {
//                                Modifier.sharedElement(
//                                    rememberSharedContentState(key = "search-top-bar-input"),
//                                    animatedVisibilityScope = animatedVisibilityScope,
//                                )
//                            } else {
//                                Modifier
//                            },
//                            query = "",
//                            onQueryChange = {},
//                            onSearch = {},
//                            expanded = false,
//                            onExpandedChange = {
//                                Log.d("COMPONENT", it.toString())
//                                if (it) {
//                                    onClick()
//                                }
//                            },
//                            placeholder = { Text(stringResource(R.string.search)) },
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = ImageVector.vectorResource(R.drawable.magnifying_glass_solid),
//                                    contentDescription = null
//                                )
//                            },
//                        )
//                    },
//                    expanded = false,
//                    onExpandedChange = {},
//                ) {}
            },
            scrollBehavior = scrollBehavior,
        )
    }
}