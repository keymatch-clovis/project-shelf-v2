package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_shelf.framework.ui.screen.ClientsScreen
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.InvoicesScreen
import com.example.project_shelf.framework.ui.screen.product.ProductsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = startDestination.path, modifier) {
        composable(Destination.PRODUCT.path) {
            ProductsScreen()
        }
        composable(Destination.CLIENT.path) {
            ClientsScreen()
        }
        composable(Destination.INVOICE.path) {
            InvoicesScreen()
        }
        composable(Destination.CONFIG.path) {
            ConfigScreen()
        }
    }
}
