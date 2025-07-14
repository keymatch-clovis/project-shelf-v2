package com.example.project_shelf.framework.ui.components.list_item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerDto

@Composable
fun CustomerListItem(
    dto: CustomerDto,
    onClick: (CustomerDto) -> Unit,
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
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = dto.businessName.ifBlank { "penesito" },
                )
            },
            trailingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chevron_right_solid),
                    contentDescription = null
                )
            },
        )
    }
}