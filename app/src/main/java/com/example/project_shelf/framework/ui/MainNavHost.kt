package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.DeletionViewModel
import com.example.project_shelf.adapter.view_model.EditProductViewModel
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
    val viewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided" }
    val deletionViewModel: DeletionViewModel = hiltViewModel(viewModelStoreOwner)

    NavHost(navController, startDestination = startDestination.path, modifier) {
        composable(Destination.PRODUCT.path) {
            ProductsScreen(
                viewModel = hiltViewModel(),
                deletionViewModel = deletionViewModel,
                onProductCreate = {
                    // If the user wants to create a product, we want to clear the deletion view
                    // model. This prevents the user from restoring a product that might clash with
                    // the new one.
                    deletionViewModel.clear()

                    navController.navigate(Destination.CREATE_PRODUCT.path)
                },
                onProductEdit = {
                    // If the user wants to edit a product, we want to clear the deletion view
                    // model. This prevents the user from restoring a product that might clash with
                    // the edited one.
                    deletionViewModel.clear()

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
            CreateProductScreen(
                onDismissRequest = { navController.popBackStack() },
            )
        }

        dialog<ProductDto>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            val product: ProductDto = it.toRoute()

            EditProductScreen(
                viewModel = hiltViewModel<EditProductViewModel, EditProductViewModel.Factory> {
                    it.create(product)
                },
                deletionViewModel = deletionViewModel,
                onDismissRequest = { navController.popBackStack() },
                onDeleteRequest = { navController.popBackStack() },
            )
        }
    }
}
