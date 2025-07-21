package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    onDismissRequest: () -> Unit,
) {
    // This box is used to render the search bars over all the content. If this is not this way, we
    // might have problems showing the contents correctly.
    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    title = { Text(stringResource(R.string.invoice_create)) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.x),
                                contentDescription = null
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            SecondaryTabRow(
                modifier = Modifier.padding(innerPadding),
                selectedTabIndex = 0,
            ) {
                Tab(selected = true, onClick = {}, text = { Text("testing") })
                Tab(selected = false, onClick = {}, text = { Text("testing") })
            }
        }
    }
}