package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.project_shelf.adapter.view_model.MainViewModel
import com.example.project_shelf.adapter.view_model.customer.CustomerDeletionViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel
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
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainNavHost(
    mainViewModel: MainViewModel,
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
        // NOTE: We need nested graphs here, as we want to share a view model between some routes,
        //  but have it constrained to these views only.
        //  https://developer.android.com/guide/navigation/design/nested-graphs
        //  https://developer.android.com/develop/ui/compose/libraries?authuser=1#hilt-navigation
        //  https://stackoverflow.com/questions/68548488/sharing-viewmodel-within-jetpack-compose-navigation
        navigation(
            route = MainDestination.PRODUCT.route,
            startDestination = Destination.PRODUCT_LIST.route,
        ) {
            composable(Destination.PRODUCT_LIST.route) {
                ProductListScreen(
                    viewModel = hiltViewModel(),
                    productDeletionViewModel = productDeletionViewModel,
                    onRequestCreate = {
                        // If we get a create request, we want to clear the deletion view model. This
                        // prevents the user from restoring an object that might have been deleted
                        // before.
                        productDeletionViewModel.clear()
                        navController.navigate(Destination.PRODUCT_CREATE.route)
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

            composable(Destination.PRODUCT_CREATE.route) {
                CreateProductScreen(
                    viewModel = hiltViewModel(),
                    onDismissed = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() },
                )
            }

//            composable(Destination.PRODUCT_EDIT.route) {
//                val product: ProductDto = it.toRoute()
//
//                EditProductScreen(
//                    viewModel = hiltViewModel<EditProductViewModel, EditProductViewModel.Factory> {
//                        it.create(product)
//                    },
//                    productDeletionViewModel = productDeletionViewModel,
//                    onDismissRequest = { navController.popBackStack() },
//                    onDeleteRequest = { navController.popBackStack() },
//                )
//            }
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
                    navController.navigate(Destination.CUSTOMER_CREATE.route)
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
        composable(Destination.CUSTOMER_CREATE.route) {
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
            composable(Destination.INVOICE_LIST.route) { backStackEntry ->
                val invoiceDraftViewModel =
                    backStackEntry.sharedViewModel<InvoiceDraftViewModel>(navController)

                InvoiceListScreen(
                    viewModel = hiltViewModel(),
                    onRequestEdit = {},
                    onRequestCreate = {
                        // Before navigating to the create invoice route, we need to set the current
                        navController.navigate(Destination.INVOICE_CREATE.route)
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
                    onDismissed = { navController.popBackStack() },
                    onSelectedDraft = {
                        navController.popBackStack()
                        navController.navigate(Destination.INVOICE_CREATE.route)
                    },
                )
            }

            composable(Destination.INVOICE_CREATE.route) { backStackEntry ->
                val draftViewModel =
                    backStackEntry.sharedViewModel<InvoiceDraftViewModel>(navController)

                val viewModel =
                    hiltViewModel<CreateInvoiceViewModel, CreateInvoiceViewModel.Factory> {
                        it.create(draftViewModel.currentDraft.consume())
                    }

                CreateInvoiceScreen(
                    draftViewModel = backStackEntry.sharedViewModel(navController),
                    viewModel = viewModel,
                    onRequestDismiss = {
                        navController.popBackStack()
                    },
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
            LaunchedEffect(Unit) {
                mainViewModel.eventFlow.collectLatest {
                    when (it) {
                        is MainViewModel.Event.Loaded -> navController.popBackStack()
                    }
                }
            }

            LoadingScreen(viewModel = mainViewModel)
        }
    }
}
