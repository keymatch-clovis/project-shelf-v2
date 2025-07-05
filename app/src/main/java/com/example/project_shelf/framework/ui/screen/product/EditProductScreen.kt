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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductViewModel
import com.example.project_shelf.framework.ui.components.dialog.AlertDialog
import com.example.project_shelf.framework.ui.components.form.EditProductForm
import com.example.project_shelf.framework.ui.getStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: EditProductViewModel,
    onDismissRequest: () -> Unit,
) {
    val nameState = viewModel.name.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    val inputState = viewModel.inputState.collectAsState()
    val validationState = viewModel.validationState.collectAsState()
    val isValid = viewModel.isValid.collectAsState()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                is EditProductViewModel.Event.ProductUpdated -> Toast.makeText(
                    context,
                    context.getText(R.string.product_updated),
                    Toast.LENGTH_SHORT,
                ).show()

                is EditProductViewModel.Event.ProductDeleted -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getText(R.string.product_deleted).toString(),
                            actionLabel = context.getText(R.string.undo).toString(),
                            duration = SnackbarDuration.Long,
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            ImageVector.vectorResource(R.drawable.trash_can_regular),
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
                onDismissRequest = { viewModel.closeConfirmDeletionDialog() },
                onAcceptRequest = { viewModel.delete() },
            )
        }

        EditProductForm(
            innerPadding = innerPadding,

            name = nameState.value,
            nameErrors = validationState.value.nameErrors.map { it.getStringResource() },
            onNameChange = { viewModel.updateName(it) },

            price = inputState.value.price,
            priceErrors = validationState.value.priceErrors.map { it.getStringResource() },
            onPriceChange = { viewModel.updatePrice(it) },

            stock = inputState.value.stock,
            stockErrors = validationState.value.stockErrors.map { it.getStringResource() },
            onStockChange = { viewModel.updateStock(it) },
        )
    }
}