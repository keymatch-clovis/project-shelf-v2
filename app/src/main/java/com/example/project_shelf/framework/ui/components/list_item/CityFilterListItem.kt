package com.example.project_shelf.framework.ui.components.list_item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.project_shelf.adapter.dto.ui.CityFilterDto

@Composable
fun CityFilterListItem(
    dto: CityFilterDto,
    onClick: (CityFilterDto) -> Unit,
) {
    Surface(
        onClick = { onClick(dto) },
    ) {
        ListItem(
            modifier = Modifier.fillMaxWidth(),
            headlineContent = {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = dto.name,
                )
            },
            supportingContent = {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = dto.department,
                )
            },
        )
    }
}