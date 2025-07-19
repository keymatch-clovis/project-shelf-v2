package com.example.project_shelf.framework.ui.components.list_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.ProductDto

@Composable
fun ProductListItem(
    dto: ProductDto,
    onClick: (ProductDto) -> Unit,
) {
    Surface(
        onClick = { onClick(dto) },
    ) {
        ListItem(
            modifier = Modifier.fillMaxWidth(),
            headlineContent = {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = dto.name,
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Inventory2,
                        contentDescription = "",
                        modifier = Modifier.width(20.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = dto.stock,
                    )
                }
            },
            trailingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                    contentDescription = null
                )
            },
        )
    }
}