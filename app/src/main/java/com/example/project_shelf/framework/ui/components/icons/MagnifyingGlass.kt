package com.example.project_shelf.framework.ui.components.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

@Composable
fun MagnifyingGlass() {
    Icon(
        // https://m3.material.io/components/icon-buttons/specs
        modifier = Modifier.size(24.dp),
        imageVector = ImageVector.vectorResource(R.drawable.search),
        contentDescription = null,
    )
}