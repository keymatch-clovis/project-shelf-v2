package com.example.project_shelf.adapter.presenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.project_shelf.adapter.view_model.ProductsViewModel

@Preview
@Composable
fun ProductList() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        repeat(10) {
            Text("Testing")
        }
    }
}