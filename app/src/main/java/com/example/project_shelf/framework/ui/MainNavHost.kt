package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.customer.CustomerDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.EditProductViewModel
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.LoadingScreen
import com.example.project_shelf.framework.ui.screen.customer.CreateCustomerScreen
import com.example.project_shelf.framework.ui.screen.customer.CustomersScreen
import com.example.project_shelf.framework.ui.screen.invoice.CreateInvoiceScreen
import com.example.project_shelf.framework.ui.screen.invoice.InvoiceListScreen
import com.example.project_shelf.framework.ui.screen.product.CreateProductScreen
import com.example.project_shelf.framework.ui.screen.product.EditProductScreen
import com.example.project_shelf.framework.ui.screen.product.ProductListScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: MainDestination,
    modifier: Modifier = Modifier,
) {
    val viewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided" }

    // Deletion view models. Used to pass information between dialogs and screens.
    val productDeletionViewModel: ProductDeletionViewModel = hiltViewModel(viewModelStoreOwner)
    val customerDeletionViewModel: CustomerDeletionViewModel = hiltViewModel(viewModelStoreOwner)

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination.route,
    ) {
        composable(MainDestination.PRODUCT.route) {
            ProductListScreen(
                viewModel = hiltViewModel(),
                productDeletionViewModel = productDeletionViewModel,
                onProductCreate = {
                    // If we get a create request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    productDeletionViewModel.clear()
                    navController.navigate(Destination.CREATE_PRODUCT.route)
                },
                onProductEdit = {
                    // If we get an edit request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    productDeletionViewModel.clear()
                    navController.navigate(it)
                },
            )
        }

        composable(MainDestination.CUSTOMER.route) {
            CustomersScreen(
                viewModel = hiltViewModel(),
                deletionViewModel = customerDeletionViewModel,
                searchViewModel = hiltViewModel(),
                onCreateRequest = {
                    // If we get a create request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    customerDeletionViewModel.clear()
                    navController.navigate(Destination.CREATE_CUSTOMER.route)
                },
                onEditRequest = {
                    // If we get an edit request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    customerDeletionViewModel.clear()
                    navController.navigate(it)
                })
        }
        /// Invoice Related
        composable(MainDestination.INVOICE.route) {
            InvoiceListScreen(
                viewModel = hiltViewModel(),
                onRequestEdit = {},
                onRequestCreate = {
                    navController.navigate(Destination.CREATE_INVOICE.route)
                },
            )
        }
        composable(Destination.CREATE_INVOICE.route) {
            CreateInvoiceScreen(
                onDismissRequest = {},
            )
        }

        composable(MainDestination.CONFIG.route) {
            ConfigScreen()
        }

        /// Dialogs
        // Loading Related
        dialog(
            route = Destination.LOADING.route,
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        ) {
            LoadingScreen(onLoadingDone = { navController.popBackStack() })
        }


        // Product Related
        dialog(
            Destination.CREATE_PRODUCT.route,
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
                productDeletionViewModel = productDeletionViewModel,
                onDismissRequest = { navController.popBackStack() },
                onDeleteRequest = { navController.popBackStack() },
            )
        }

        // Customer Related
        dialog(
            Destination.CREATE_CUSTOMER.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            CreateCustomerScreen(
                viewModel = hiltViewModel(),
                onDismissRequest = { navController.popBackStack() },
            )
        }

    }
}
