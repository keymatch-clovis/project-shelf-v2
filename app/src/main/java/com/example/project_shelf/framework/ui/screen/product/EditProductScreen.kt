package com.example.project_shelf.framework.ui.screen.product

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.EditProductViewModel
import com.example.project_shelf.framework.ui.components.dialog.AlertDialog
import com.example.project_shelf.framework.ui.components.form.EditProductForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    viewModel: EditProductViewModel,
    onDismissRequest: () -> Unit,
) {
    val state = viewModel.uiState.collectAsState()

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
                            ImageVector.vectorResource(R.drawable.trash_can_regular),
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    }
                    Button(
                        enabled = state.value.isValid,
                        onClick = {},
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.value.showConfirmDeletionDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeConfirmDeletionDialog() },
                onAcceptRequest = {
                    viewModel.delete(onDismissRequest)
                },
            )
        }

        EditProductForm(
            innerPadding = innerPadding,
            state = state,
            onNameChange = { viewModel.updateName(it) },
            onPriceChange = { viewModel.updatePrice(it) },
            onCountChange = { viewModel.updateCount(it) },
        )
    }
}