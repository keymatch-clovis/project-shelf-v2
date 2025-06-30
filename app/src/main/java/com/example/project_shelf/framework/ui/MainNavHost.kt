package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.example.project_shelf.adapter.view_model.EditProductViewModel
import com.example.project_shelf.adapter.view_model.ProductUiState
import com.example.project_shelf.framework.ui.screen.ClientsScreen
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.InvoicesScreen
import com.example.project_shelf.framework.ui.screen.product.CreateProductScreen
import com.example.project_shelf.framework.ui.screen.product.EditProductScreen
import com.example.project_shelf.framework.ui.screen.product.ProductsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = startDestination.path, modifier) {
        composable(Destination.PRODUCT.path) {
            ProductsScreen(
                onProductCreate = {
                    navController.navigate(Destination.CREATE_PRODUCT.path)
                },
                onProductEdit = {
                    navController.navigate(it)
                },
            )
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

        /// Dialogs
        dialog(
            Destination.CREATE_PRODUCT.path,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CreateProductScreen(onDismissRequest = {
                navController.popBackStack()
            })
        }

        dialog<ProductUiState>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            val product: ProductUiState = it.toRoute()
            EditProductScreen(
                viewModel = hiltViewModel<EditProductViewModel, EditProductViewModel.Factory> {
                    it.create(product)
                },
                onDismissRequest = {
                    navController.popBackStack()
                },
            )
        }
    }
}
