package com.example.project_shelf.framework.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.project_shelf.BuildConfig
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.ConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.configuration)) })
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (BuildConfig.DEBUG == true) {
                ListItem(
                    modifier = Modifier.clickable(onClick = {
                        viewModel.removeAllData {
                            snackbarHostState.showSnackbar("[DEBUG] All data removed")
                        }
                    }),
                    leadingContent = {
                        Icon(Icons.Rounded.DeveloperMode, contentDescription = null)
                    },
                    headlineContent = { Text("[DEBUG] Remove all data") },
                )
                ListItem(
                    modifier = Modifier.clickable(onClick = {
                        viewModel.loadTestProducts {
                            snackbarHostState.showSnackbar("[DEBUG] Test Products Loaded")
                        }
                    }),
                    leadingContent = {
                        Icon(Icons.Rounded.DeveloperMode, contentDescription = null)
                    },
                    headlineContent = { Text("[DEBUG] Load Test Products") },
                )
            }
        }
    }
}