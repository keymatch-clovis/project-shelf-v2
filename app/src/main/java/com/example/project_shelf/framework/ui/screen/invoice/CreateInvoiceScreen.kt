package com.example.project_shelf.framework.ui.screen.invoice

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.project_shelf.R
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceDestination
import com.example.project_shelf.framework.ui.nav_host.CreateInvoiceNavHost

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceScreen(
    onDismissRequest: () -> Unit,
) {
    val navHostController = rememberNavController()
    val startDestination = CreateInvoiceDestination.DETAILS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedDestination
            ) {
                CreateInvoiceDestination.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
                            navHostController.popBackStack(
                                navHostController.graph.startDestinationId,
                                true,
                            )
                            navHostController.navigate(route = destination.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedDestination = index
                        },
                        text = { Text(stringResource(destination.labelStringResource)) },
                    )
                }
            }
            CreateInvoiceNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                navHostController = navHostController,
                startDestination = CreateInvoiceDestination.DETAILS,
            )
            HorizontalDivider()
            // https://m3.material.io/components/lists/specs
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Invoice Number: 1")
                Text("Customer: testing")
                Text("TOTAL: 12304")
            }
        }
    }
}