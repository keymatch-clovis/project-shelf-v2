package com.example.project_shelf.framework.ui.screen.invoice

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftListViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel
import com.example.project_shelf.framework.ui.components.dialog.AlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDraftListScreen(
    draftViewModel: InvoiceDraftViewModel,
    viewModel: InvoiceDraftListViewModel,
    onSelectedDraft: () -> Unit,
    onDismissed: () -> Unit,
) {
    /// Scroll related
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()

    /// Alert dialog related
    var showConfirmDeletionDialog by remember { mutableStateOf(false) }

    /// State related
    val state = viewModel.state.collectAsState()

    BackHandler(enabled = state.value.isEditing) {
        // Exit first the edition mode, so we don't show weird artifacts or glitches on the UI.
        viewModel.exitEdition()

        viewModel.clearCheckedItems()
    }

    if (showConfirmDeletionDialog) {
        AlertDialog(
            headerTextResource = R.string.drafts_delete_selected_alert,
            bodyTextResource = R.string.drafts_delete_selected_alert_message,
            onDismissRequest = { showConfirmDeletionDialog = false },
            onAcceptRequest = {
                viewModel.deleteMarkedItems()
                showConfirmDeletionDialog = false
            },
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    if (state.value.isEditing) {
                        Text(state.value.checkedItems.size.toString())
                    } else {
                        Text(stringResource(R.string.invoice_drafts))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.value.isEditing) {
                                // Exit first the edition mode, so we don't show weird artifacts or
                                // glitches on the UI.
                                viewModel.exitEdition()

                                viewModel.clearCheckedItems()
                            } else {
                                onDismissed()
                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_left),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (state.value.isEditing) {
                        IconButton(onClick = { showConfirmDeletionDialog = true }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.trash),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { viewModel.checkAllItems() }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.check_check),
                                contentDescription = null
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.value.drafts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.surfaceDim,
                            imageVector = ImageVector.vectorResource(R.drawable.package_open),
                            contentDescription = null,
                        )
                        Text(
                            color = MaterialTheme.colorScheme.surfaceDim,
                            text = stringResource(R.string.drafts_none),
                        )
                    }
                }
            }

            // TODO: Convert this to a lazy column, like `CustomList`.
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                state.value.drafts.forEachIndexed { index, item ->
                    Surface(
                        modifier = Modifier.combinedClickable(
                            interactionSource = null,
                            indication = ripple(),
                            onClick = {
                                if (state.value.isEditing) {
                                    // NOTE: What do you think? Should this be in the view model? Or
                                    //  here is better?
                                    if (state.value.checkedItems.contains(item.id)) {
                                        viewModel.uncheckItem(item.id)
                                    } else {
                                        viewModel.checkItem(item.id)
                                    }
                                } else {
                                    draftViewModel.setCurrentDraft(item)
                                    onSelectedDraft()
                                }
                            },
                            onLongClick = {
                                // If the user is already editing, just ignore the long press. I
                                // think it feels better.
                                if (!state.value.isEditing) {
                                    viewModel.checkItem(item.id)
                                    viewModel.enterEdition()
                                }
                            },
                        ),
                    ) {
                        ListItem(
                            trailingContent = {
                                if (state.value.isEditing) {
                                    if (state.value.checkedItems.contains(item.id)) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            imageVector = ImageVector.vectorResource(R.drawable.check),
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                                        contentDescription = null
                                    )
                                }
                            },
                            headlineContent = { Text(item.date.toString()) },
                        )
                    }

                    if (index < state.value.drafts.size - 1) {
                        HorizontalDivider()
                    }
                }
            }

            AnimatedVisibility(
                visible = state.value.isLoading,
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