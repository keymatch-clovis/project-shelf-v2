package com.example.project_shelf.framework.ui.screen.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.product.EditProductScreenViewModel
import com.example.project_shelf.framework.ui.components.dialog.AlertDialog
import com.example.project_shelf.framework.ui.components.text_field.CustomTextField
import com.example.project_shelf.framework.ui.getStringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    state: EditProductScreenViewModel.State,
    callback: EditProductScreenViewModel.Callback,
) {
    /// Ephemeral state related
    var isShowingConfirmDeletionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.padding(horizontal = 4.dp),
                title = { Text(stringResource(R.string.product_edit)) },
                navigationIcon = {
                    IconButton(onClick = { callback.onNavigateBack() }) {
                        Icon(Icons.Rounded.Clear, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isShowingConfirmDeletionDialog = true },
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.trash),
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    }
                    Button(
                        enabled = state.isValid,
                        onClick = { callback.onRequestEditProduct() },
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    /// Name
                    CustomTextField(
                        required = true,
                        value = state.name.value,
                        errors = state.name.errors.map { it.getStringResource() },
                        onValueChange = { callback.onUpdateName(it) },
                        label = R.string.name,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                    )
                    /// Default price
                    CustomTextField(
                        value = state.defaultPrice.value,
                        errors = state.defaultPrice.errors.map { it.getStringResource() },
                        onValueChange = { callback.onUpdateDefaultPrice(it) },
                        label = R.string.default_price,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    /// Stock
                    CustomTextField(
                        value = state.stock.value,
                        errors = state.stock.errors.map { it.getStringResource() },
                        onValueChange = { callback.onUpdateStock(it) },
                        label = R.string.amount,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done
                        ),
                    )
                }
            }
        }
    }

    if (isShowingConfirmDeletionDialog) {
        AlertDialog(
            headerTextResource = R.string.product_delete_alert,
            onDismissRequest = { isShowingConfirmDeletionDialog = false },
            onAcceptRequest = { callback.onRequestDeleteProduct() },
        )
    }
}
