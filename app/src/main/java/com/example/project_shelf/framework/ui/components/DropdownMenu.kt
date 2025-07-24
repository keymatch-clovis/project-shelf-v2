package com.example.project_shelf.framework.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu as ComposeDropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

@Composable
fun DropdownMenu() {
    var expanded by remember { mutableStateOf(false) }

    // https://developer.android.com/develop/ui/compose/components/menu
    Box() {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ellipsis_vertical),
                contentDescription = null,
            )
        }
        ComposeDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {},
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.pencil),
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(R.string.edit)) },
            )
            DropdownMenuItem(
                onClick = {},
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.trash),
                        contentDescription = null,
                    )
                },
                text = { Text(stringResource(R.string.delete)) },
            )
        }
    }
}