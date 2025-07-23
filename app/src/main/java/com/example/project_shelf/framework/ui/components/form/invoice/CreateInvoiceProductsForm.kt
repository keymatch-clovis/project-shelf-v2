package com.example.project_shelf.framework.ui.components.form.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.ProductFilterDto
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceProductsForm(
    products: List<ProductFilterDto> = listOf(ProductFilterDto(id = 1, name = "test")),
    emitter: MutableSharedFlow<CreateInvoiceViewModel.Event>,
) {
    /// Related to event emitting
    val scope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // https://m3.material.io/components/lists/specs
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            FilledIconButton(
                onClick = {
                    scope.launch { emitter.emit(CreateInvoiceViewModel.Event.OpenSearchProduct) }
                },
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    contentDescription = null,
                    imageVector = ImageVector.vectorResource(R.drawable.plus),
                )
            }
        }
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceDim)
                .verticalScroll(rememberScrollState()),
        ) {
            products.forEach {
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    headlineContent = {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            text = it.name,
                        )
                    },
                    supportingContent = {
                        Text("supporting")
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = {}) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = null,
                                    imageVector = ImageVector.vectorResource(R.drawable.pencil),
                                )
                            }
                            IconButton(onClick = {}) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = null,
                                    imageVector = ImageVector.vectorResource(R.drawable.trash),
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}