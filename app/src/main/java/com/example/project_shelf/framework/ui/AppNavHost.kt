package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.project_shelf.framework.ui.screen.ClientsScreen
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.InvoicesScreen
import com.example.project_shelf.framework.ui.screen.product.ProductsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = startDestination.path, modifier) {
        navigation(
            route = Destination.PRODUCT.path,
            startDestination = Destination.PRODUCT_LIST.path
        ) {
            composable(Destination.PRODUCT_LIST.path) {
                ProductsScreen()
            }
        }

        navigation(
            route = Destination.CLIENT.path,
            startDestination = Destination.CLIENT_LIST.path
        ) {
            composable(Destination.CLIENT_LIST.path) {
                ClientsScreen()
            }
        }

        navigation(
            route = Destination.INVOICE.path,
            startDestination = Destination.INVOICE_LIST.path
        ) {
            composable(Destination.INVOICE_LIST.path) {
                InvoicesScreen()
            }
        }

        navigation(
            route = Destination.CONFIG.path,
            startDestination = Destination.CONFIG_LIST.path
        ) {
            composable(Destination.CONFIG_LIST.path) {
                ConfigScreen()
            }
        }
    }
}