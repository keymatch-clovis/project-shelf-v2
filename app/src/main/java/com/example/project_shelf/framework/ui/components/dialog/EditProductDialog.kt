package com.example.project_shelf.framework.ui.components.dialog

import android.util.Log
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductViewModel
import com.example.project_shelf.adapter.view_model.ProductUiState
import com.example.project_shelf.framework.ui.components.form.EditProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    viewModel: EditProductViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit = {},
    product: ProductUiState,
) {
    Log.d("DIALOG", product.toString())
    // IMPORTANT: Set the product before starting.
    viewModel.setProduct(product)

    val state = viewModel.uiState.collectAsState()

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    },
                    title = { Text(stringResource(R.string.product_edit)) },
                    actions = {
                        Button(
                            enabled = state.value.isValid,
                            onClick = {}
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    },
                )
            },
        ) { innerPadding ->
            EditProductForm(
                innerPadding = innerPadding,
                state = state,
                onNameChange = { viewModel.updateName(it) },
                onPriceChange = { viewModel.updatePrice(it) },
                onCountChange = { viewModel.updateCount(it) },
            )
        }
    }
}
