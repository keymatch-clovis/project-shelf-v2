package com.example.project_shelf.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.project_shelf.ui.screen.ClientsScreen
import com.example.project_shelf.ui.screen.InvoicesScreen
import com.example.project_shelf.ui.screen.ProductsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
) {
    NavHost(navController, startDestination = startDestination.path) {
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
    }
}