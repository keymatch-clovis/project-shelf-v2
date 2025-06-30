package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.CreateProductViewModel
import com.example.project_shelf.framework.ui.components.form.CreateProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: CreateProductViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
) {
    val state = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.product_create)) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
                    }
                },
                actions = {
                    Button(
                        enabled = state.value.isValid,
                        onClick = {
                            viewModel.create {
                                onDismissRequest()
                            }
                        },
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        CreateProductForm(
            innerPadding = innerPadding,
            state = state,
            onUpdateName = { viewModel.updateName(it) },
            onUpdatePrice = { viewModel.updatePrice(it) },
            onUpdateCount = { viewModel.updateCount(it) },
        )
    }
}