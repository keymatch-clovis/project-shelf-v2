package com.example.project_shelf.framework.ui.screen.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftListViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDraftListScreen(
    draftViewModel: InvoiceDraftViewModel,
    viewModel: InvoiceDraftListViewModel,
) {
    /// Scroll related
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()

    /// List related
    val isLoading = viewModel.isLoading.collectAsState()
    val drafts = viewModel.drafts.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(stringResource(R.string.invoice_drafts)) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                drafts.value.forEachIndexed { index, item ->
                    Surface(
                        onClick = {},
                    ) {
                        ListItem(
                            headlineContent = { Text(item.toString()) },
                        )
                    }

                    if (index < drafts.value.size - 1) {
                        HorizontalDivider()
                    }
                }
            }

            AnimatedVisibility(
                visible = isLoading.value,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}