package com.example.project_shelf.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import com.example.project_shelf.adapter.view_model.ProductUiState

class ProductParameterProvider : PreviewParameterProvider<ProductUiState> {
    override val values = sequenceOf(
        ProductUiState(
            name = "Testing",
            price = "1234.56",
            count = "4321",
        )
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ProductListItem(
    @PreviewParameter(ProductParameterProvider::class) product: ProductUiState,
    onItemClicked: (item: ProductUiState) -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(product) },
        headlineContent = {
            Text(
                style = MaterialTheme.typography.titleLargeEmphasized,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                text = product.name,
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
                    text = product.price,
                )
            }
        },
        trailingContent = {
            Icon(Icons.Rounded.ChevronRight, contentDescription = null)
        }
    )
}