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
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.project_shelf.adapter.dto.ui.ProductDto
import com.example.project_shelf.adapter.view_model.customer.CustomerDeletionViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.adapter.view_model.product.ProductDeletionViewModel
import com.example.project_shelf.adapter.view_model.product.EditProductViewModel
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.LoadingScreen
import com.example.project_shelf.framework.ui.screen.customer.CreateCustomerScreen
import com.example.project_shelf.framework.ui.screen.customer.CustomerListScreen
import com.example.project_shelf.framework.ui.screen.invoice.CreateInvoiceScreen
import com.example.project_shelf.framework.ui.screen.invoice.InvoiceListScreen
import com.example.project_shelf.framework.ui.screen.invoice.InvoiceDraftListScreen
import com.example.project_shelf.framework.ui.screen.product.CreateProductScreen
import com.example.project_shelf.framework.ui.screen.product.EditProductScreen
import com.example.project_shelf.framework.ui.screen.product.ProductListScreen
import com.example.project_shelf.framework.ui.util.navigation.sharedViewModel

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
        /// Product Related
        composable(MainDestination.PRODUCT.route) {
            ProductListScreen(
                viewModel = hiltViewModel(),
                productDeletionViewModel = productDeletionViewModel,
                onRequestCreate = {
                    // If we get a create request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    productDeletionViewModel.clear()
                    navController.navigate(Destination.CREATE_PRODUCT.route)
                },
                onRequestEdit = {
                    // If we get an edit request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    productDeletionViewModel.clear()
                    navController.navigate(it)
                },
            )
        }
        composable(Destination.CREATE_PRODUCT.route) {
            CreateProductScreen(
                viewModel = hiltViewModel(),
                onDismissed = { navController.popBackStack() },
                onCreated = { navController.popBackStack() },
            )
        }

        /// Customer related
        composable(MainDestination.CUSTOMER.route) {
            CustomerListScreen(
                viewModel = hiltViewModel(),
                deletionViewModel = customerDeletionViewModel,
                onRequestCreate = {
                    // If we get a create request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    customerDeletionViewModel.clear()
                    navController.navigate(Destination.CREATE_CUSTOMER.route)
                },
                onRequestEdit = {
                    // If we get an edit request, we want to clear the deletion view model. This
                    // prevents the user from restoring an object that might have been deleted
                    // before.
                    customerDeletionViewModel.clear()
                    navController.navigate(it)
                },
            )
        }
        composable(Destination.CREATE_CUSTOMER.route) {
            CreateCustomerScreen(
                viewModel = hiltViewModel(),
                onDismissed = { navController.popBackStack() },
                onCreated = { navController.popBackStack() },
            )
        }

        /// Invoice Related
        // NOTE: We need nested graphs here, as we want to share a view model between some routes,
        //  but have it constrained to these views only.
        //  https://developer.android.com/guide/navigation/design/nested-graphs
        //  https://developer.android.com/develop/ui/compose/libraries?authuser=1#hilt-navigation
        //  https://stackoverflow.com/questions/68548488/sharing-viewmodel-within-jetpack-compose-navigation
        navigation(
            route = MainDestination.INVOICE.route,
            startDestination = Destination.INVOICE_LIST.route,
        ) {
            composable(Destination.INVOICE_LIST.route) {
                InvoiceListScreen(
                    viewModel = hiltViewModel(),
                    onRequestEdit = {},
                    onRequestCreate = {
                        navController.navigate(Destination.CREATE_INVOICE.route)
                    },
                    onNavigateSaved = {
                        navController.navigate(Destination.INVOICE_DRAFT_LIST.route)
                    },
                )
            }

            composable(Destination.INVOICE_DRAFT_LIST.route) { backStackEntry ->
                InvoiceDraftListScreen(
                    draftViewModel = backStackEntry.sharedViewModel(navController),
                    viewModel = hiltViewModel(),
                )
            }

            composable(Destination.CREATE_INVOICE.route) { backStackEntry ->
                val viewModel = hiltViewModel<CreateInvoiceViewModel>()

                CreateInvoiceScreen(
                    draftViewModel = backStackEntry.sharedViewModel(navController),
                    viewModel = viewModel,
                    onRequestDismiss = { navController.popBackStack() },
                )
            }
        }

        /// Config related
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
    }
}
