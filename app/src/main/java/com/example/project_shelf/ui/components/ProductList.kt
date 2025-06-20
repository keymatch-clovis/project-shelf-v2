package com.example.project_shelf.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.project_shelf.adapter.view_model.ProductUiState
import androidx.compose.ui.unit.dp

@Composable
fun ProductList(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    nestedScrollConnection: NestedScrollConnection,
) {
    LazyColumn(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .fillMaxWidth(),
        contentPadding = contentPadding,
    ) {
        items(20) {
            ProductListItem(
                product = ProductUiState(
                    name = "Testing"
                )
            )
        }
    }
}