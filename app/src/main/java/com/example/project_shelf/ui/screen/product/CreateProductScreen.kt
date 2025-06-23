package com.example.project_shelf.ui.screen.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.CreateProductViewModel
import com.example.project_shelf.ui.components.form.CreateProductForm
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel = hiltViewModel(),
    onCreateProduct: () -> Unit = {},
) {
    val validationState = viewModel.validationState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = null)
            },
            title = {
                Text(stringResource(R.string.product_create))
            },
        )
    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.large,
                enabled = validationState.value?.isValid == true,
                onClick = {},
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                )
            }
        }
    }) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(Modifier.padding(16.dp)) {
                CreateProductForm(viewModel)
            }
        }
    }
}