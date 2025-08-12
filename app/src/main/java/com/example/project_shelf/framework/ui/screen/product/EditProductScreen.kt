package com.example.project_shelf.framework.ui.screen.product

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.EditProductViewModel
import com.example.project_shelf.framework.ui.components.dialog.AlertDialog
import com.example.project_shelf.framework.ui.components.form.EditProductForm
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: EditProductViewModel,
    productDeletionViewModel: ProductDeletionViewModel,
    onDismissRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    /// Input state related
    val inputState = viewModel.inputState.collectAsState()
    val isValid = viewModel.isValid.collectAsState()

    /// UI state related
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is EditProductViewModel.Event.ProductUpdated -> Toast
                    .makeText(
                        context,
                        context.getText(R.string.product_updated),
                        Toast.LENGTH_SHORT,
                    )
                    .show()

                is EditProductViewModel.Event.ProductMarkedForDeletion -> onDeleteRequest()
            }
        }
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.product_edit)) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openConfirmDeletionDialog() },
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.trash),
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    }
                    Button(
                        enabled = isValid.value,
                        onClick = { viewModel.edit() },
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.value.showConfirmDeletionDialog) {
            AlertDialog(
                headerTextResource = R.string.product_delete_alert,
                onDismissRequest = { viewModel.closeConfirmDeletionDialog() },
                onAcceptRequest = {
                    productDeletionViewModel.markProductForDeletion(viewModel.product)
                    onDeleteRequest()
                },
            )
        }

        // TODO: FIX THIS ?????
        EditProductForm(
            innerPadding = innerPadding,

            name = inputState.value.name.value ?: "",
            nameErrors = inputState.value.name.errors.map { it.getStringResource() },
            onNameChange = { viewModel.updateName(it) },

            price = inputState.value.price.value ?: "",
            priceErrors = inputState.value.price.errors.map { it.getStringResource() },
            onPriceChange = { viewModel.updatePrice(it) },

            stock = inputState.value.stock.value ?: "",
            stockErrors = inputState.value.stock.errors.map { it.getStringResource() },
            onStockChange = { viewModel.updateStock(it) },
        )
    }
}