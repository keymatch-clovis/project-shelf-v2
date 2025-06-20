package com.example.project_shelf.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Preview
@Composable
fun ProductListItem(
    @PreviewParameter(ProductParameterProvider::class)
    product: ProductUiState
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("PRODUCT-LIST-ITEM", "clicked")
            },
        headlineContent = {
            Text(product.name)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.AttachMoney,
                    contentDescription = "",
                    modifier = Modifier.width(10.dp)
                )
                Text(product.price, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Category,
                    contentDescription = "",
                    modifier = Modifier.width(10.dp)
                )
                Text(product.count, fontSize = 10.sp)
            }
        },
    )
}