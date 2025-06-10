package com.example.project_shelf.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.project_shelf.R

@Preview()
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val startDestination = Destination.PRODUCT
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedDestination == Destination.PRODUCT.ordinal,
                    onClick = {
                        navController.navigate(route = Destination.PRODUCT.path)
                        selectedDestination = Destination.PRODUCT.ordinal
                    },
                    icon = { Icon(Icons.Rounded.Category, contentDescription = null) },
                    label = { Text(stringResource(R.string.products)) })
                NavigationBarItem(
                    selected = selectedDestination == Destination.CLIENT.ordinal,
                    onClick = {
                        navController.navigate(route = Destination.CLIENT.path)
                        selectedDestination = Destination.CLIENT.ordinal
                    },
                    icon = { Icon(Icons.Rounded.Groups, contentDescription = null) },
                    label = { Text(stringResource(R.string.clients)) })
                NavigationBarItem(
                    selected = selectedDestination == Destination.INVOICE.ordinal,
                    onClick = {
                        navController.navigate(route = Destination.INVOICE.path)
                        selectedDestination = Destination.INVOICE.ordinal
                    },
                    icon = { Icon(Icons.Rounded.Receipt, contentDescription = null) },
                    label = { Text(stringResource(R.string.invoices)) })
            }
        }) { _ ->
        AppNavHost(navController, startDestination)
    }
}