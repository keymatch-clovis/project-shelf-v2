package com.example.project_shelf.framework.ui.components.form.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceProductState
import com.example.project_shelf.framework.ui.components.DropdownMenu
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceProductsForm(
    invoiceProducts: List<InvoiceProductState>,
    emitter: MutableSharedFlow<CreateInvoiceViewModel.Event>,
) {
    /// Related to UI behavior.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    /// Related to event emitting
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Button(
                        onClick = {
                            scope.launch { emitter.emit(CreateInvoiceViewModel.Event.OpenSearchProduct) }
                        },
                    ) {
                        Icon(
                            // https://m3.material.io/components/split-button/specs
                            modifier = Modifier.size(20.dp),
                            contentDescription = null,
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                        )
                        Text(stringResource(R.string.product_add))
                    }
                }
            )
        },
        bottomBar = {
            HorizontalDivider()
            Column(modifier = Modifier.padding(8.dp)) {

                Text("test")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            /// Empty products label
            if (invoiceProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            imageVector = ImageVector.vectorResource(R.drawable.package_open),
                            contentDescription = null,
                        )
                        Text(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            text = stringResource(R.string.products_none),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .verticalScroll(rememberScrollState()),
            ) {
                invoiceProducts.forEach {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainer)
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
                            DropdownMenu()
                        },
                    )
                }
            }
        }
    }
}