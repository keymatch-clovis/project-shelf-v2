package com.example.project_shelf.framework.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource


/**
 * This custom `NestedScrollConnection` shows or hide the content based on the current user
 * scrolling behavior; If the user scrolls down, it hides. If the user scrolls up, it shows.
 */
@Composable
fun customNestedScrollConnection(show: MutableState<Boolean>): NestedScrollConnection {
    return remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    show.value = false
                }
                if (available.y > 1) {
                    show.value = true
                }
                return Offset.Zero
            }
        }
    }
}