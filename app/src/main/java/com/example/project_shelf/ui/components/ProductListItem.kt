package com.example.project_shelf.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Card {
        Column(Modifier.padding(4.dp)) {
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
        }
    }
}