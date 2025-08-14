package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.example.project_shelf.adapter.view_model.MainViewModel
import com.example.project_shelf.adapter.view_model.common.DeletionExtension
import com.example.project_shelf.adapter.view_model.common.SearchExtension
import com.example.project_shelf.adapter.view_model.customer.CreateCustomerScreenViewModel
import com.example.project_shelf.adapter.view_model.customer.CustomerListScreenViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceProductDialogViewModel
import com.example.project_shelf.adapter.view_model.invoice.CreateInvoiceScreenViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDetailsFormViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceDraftViewModel
import com.example.project_shelf.adapter.view_model.invoice.InvoiceProductListFormViewModel
import com.example.project_shelf.adapter.view_model.product.EditProductScreenViewModel
import com.example.project_shelf.adapter.view_model.product.ProductRouteViewModel
import com.example.project_shelf.framework.ui.common.navigation.sharedViewModel
import com.example.project_shelf.framework.ui.screen.ConfigScreen
import com.example.project_shelf.framework.ui.screen.LoadingScreen
import com.example.project_shelf.framework.ui.screen.customer.CreateCustomerScreen
import com.example.project_shelf.framework.ui.screen.customer.CustomerListScreen
import com.example.project_shelf.framework.ui.screen.invoice.CreateInvoiceScreen
import com.example.project_shelf.framework.ui.screen.invoice.InvoiceDraftListScreen
import com.example.project_shelf.framework.ui.screen.invoice.InvoiceListScreen
import com.example.project_shelf.framework.ui.screen.product.CreateProductScreen
import com.example.project_shelf.framework.ui.screen.product.EditProductScreen
import com.example.project_shelf.framework.ui.screen.product.ProductListScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainNavHost(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    startDestination: MainDestination,
    modifier: Modifier = Modifier,
) {
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
            composable(Destination.PRODUCT_LIST.route) { backStackEntry ->
                // NOTE: We COULD create a new view model for the route, and leave the product list
                //  view model alone. But I'm tired man.
                val routeViewModel =
                    backStackEntry.sharedViewModel<ProductRouteViewModel>(navController)

                val searchState = routeViewModel.searchExtension.state.collectAsState()
                val deletionState = routeViewModel.deletionExtension.state.collectAsState()

                ProductListScreen(
                    productList = routeViewModel.products,
                    callback = ProductRouteViewModel.Callback(
                        onRequestCreateProduct = {
                            navController.navigate(Destination.PRODUCT_CREATE.route)
                        },
                        onRequestOpenProduct = {
                            routeViewModel.setSelectedProduct(it)
                            navController.navigate(Destination.PRODUCT_EDIT.route)
                        },
                    ),
                    searchState = searchState.value,
                    searchCallback = SearchExtension.Callback(
                        onOpenSearch = { routeViewModel.searchExtension.openSearch() },
                        onCloseSearch = { routeViewModel.searchExtension.closeSearch() },
                        onUpdateQuery = { routeViewModel.searchExtension.updateQuery(it) },
                    ),
                    searchResult = routeViewModel.searchExtension.result,
                    deletionState = deletionState.value,
                    deletionCallback = DeletionExtension.Callback(
                        onSetItemPendingForDeletion = {
                            routeViewModel.deletionExtension.onSetItemPendingForDeletion(it)
                        },
                        onUnsetItemPendingForDeletion = {
                            routeViewModel.deletionExtension.onUnsetItemPendingForDeletion(it)
                        },
                        onDismissItemPendingForDeletion = {
                            routeViewModel.deletionExtension.onDismissItemPendingForDeletion(it)
                        },
                    )
                )
            }

            composable(Destination.PRODUCT_CREATE.route) {
                CreateProductScreen(
                    viewModel = hiltViewModel(),
                    onDismissed = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() },
                )
            }

            composable(Destination.PRODUCT_EDIT.route) { backStackEntry ->
                // NOTE: We COULD create a new view model for the route, and leave the product list
                //  view model alone. But I'm tired man.
                val routeViewModel =
                    backStackEntry.sharedViewModel<ProductRouteViewModel>(navController)

                val viewModel =
                    hiltViewModel<EditProductScreenViewModel, EditProductScreenViewModel.Factory> {
                        it.create(routeViewModel.consumeSelectedProduct())
                    }

                EditProductScreen(
                    state = viewModel.state.collectAsState().value,
                    callback = EditProductScreenViewModel.Callback(
                        onNavigateBack = { navController.popBackStack() },
                        onRequestDeleteProduct = {},
                        onRequestEditProduct = {},
                        onUpdateName = { viewModel.updateName(it) },
                        onUpdateDefaultPrice = { viewModel.updateDefaultPrice(it) },
                        onUpdateStock = { viewModel.updateStock(it) },
                    )
                )
            }
        }

        /// Customer related
        composable(MainDestination.CUSTOMER.route) { backStackEntry ->
            val viewModel = hiltViewModel<CustomerListScreenViewModel>()

            val state = viewModel.state.collectAsState()
            val searchState = viewModel.search.state.collectAsState()

            CustomerListScreen(
                state = state.value,
                callback = CustomerListScreenViewModel.Callback(
                    onOpenSearch = { viewModel.onOpenSearch() },
                    onCloseSearch = { viewModel.onCloseSearch() },
                    onRequestCreateCustomer = { navController.navigate(Destination.CUSTOMER_CREATE.route) },
                    onRequestEditCustomer = { TODO() },
                    onSetCustomerPendingForDeletion = { viewModel.setCustomerPendingForDeletion(it) },
                    onUnsetCustomerPendingForDeletion = {
                        viewModel.unsetCustomerPendingForDeletion(it)
                    },
                    onDismissCustomerMarkedForDeletion = {
                        viewModel.dismissCustomerMarkedForDeletion(it)
                    },
                ),
                searchState = searchState.value,
                searchCallback = SearchExtension.Callback(
                    onOpenSearch = TODO(),
                    onCloseSearch = TODO(),
                    onUpdateQuery = { viewModel.search.updateQuery(it) },
                ),
                searchResult = viewModel.search.result,
            )
        }

        composable(Destination.CUSTOMER_CREATE.route) {
            val viewModel = hiltViewModel<CreateCustomerScreenViewModel>()

            val state = viewModel.state.collectAsState()
            val citySearchState = viewModel.citySearch.state.collectAsState()

            CreateCustomerScreen(
                state = state.value,
                callback = CreateCustomerScreenViewModel.Callback(
                    onNameChange = { viewModel.updateName(it) },
                    onPhoneChange = { viewModel.updatePhone(it) },
                    onAddressChange = { viewModel.updateAddress(it) },
                    onBusinessNameChange = { viewModel.updateBusinessName(it) },
                    onCityChange = { viewModel.updateCity(it) },
                    onCreateRequest = { viewModel.create() },
                    onDismissRequest = { navController.popBackStack() },
                    openCitySearch = { viewModel.openCitySearchBar() },
                    closeCitySearch = { viewModel.closeCitySearchBar() },
                ),
                citySearchState = citySearchState.value,
                citySearchCallback = SearchExtension.Callback(
                    onOpenSearch = TODO(),
                    onCloseSearch = TODO(),
                    onUpdateQuery = { viewModel.citySearch.updateQuery(it) },
                ),
                citySearchResult = viewModel.citySearch.result,
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
                val currentDraft = TODO()

                val mainViewModel =
                    hiltViewModel<CreateInvoiceScreenViewModel, CreateInvoiceScreenViewModel.Factory> {
                        it.create(currentDraft)
                    }

                val detailsViewModel =
                    hiltViewModel<InvoiceDetailsFormViewModel, InvoiceDetailsFormViewModel.Factory> {
                        it.create(currentDraft)
                    }

                val invoiceProductListViewModel =
                    hiltViewModel<InvoiceProductListFormViewModel, InvoiceProductListFormViewModel.Factory> {
                        it.create(draftProducts = TODO())
                    }

                val createInvoiceProductDialogViewModel =
                    hiltViewModel<CreateInvoiceProductDialogViewModel>()

                val createInvoiceState = mainViewModel.state.collectAsState()
                val invoiceDetailsFormState = detailsViewModel.state.collectAsState()
                val invoiceProductListFormState = invoiceProductListViewModel.state.collectAsState()
                val createInvoiceProductDialogState =
                    createInvoiceProductDialogViewModel.state.collectAsState()

                CreateInvoiceScreen(
                    draftViewModel = backStackEntry.sharedViewModel(navController),
                    state = createInvoiceState.value,
                    callback = CreateInvoiceScreenViewModel.Callback(
                        onCloseRequest = { navController.popBackStack() },
                        onCreateRequest = {
                            mainViewModel.createInvoice(
                                customerId = invoiceDetailsFormState.value.customer.value!!.id,
                                products = invoiceProductListFormState.value.invoiceProducts,
                            )
                        },
                        onOpenCustomerSearch = { mainViewModel.openCustomerSearch() },
                        onCloseCustomerSearch = { mainViewModel.closeCustomerSearch() },
                        onOpenProductSearch = { mainViewModel.openProductSearch() },
                        onCloseProductSearch = { mainViewModel.closeProductSearch() },
                    ),
                    invoiceDetailsFormState = invoiceDetailsFormState.value,
                    invoiceDetailsFormCallback = InvoiceDetailsFormViewModel.Callback(
                        onOpenCustomerSearch = { mainViewModel.openCustomerSearch() },
                        onSetCustomer = TODO(),
                    ),
                    invoiceProductListFormState = invoiceProductListFormState.value,
                    invoiceProductListFormCallback = InvoiceProductListFormViewModel.Callback(
                        onOpenSearchProduct = { mainViewModel.openProductSearch() },
                        onAddRequest = { createInvoiceProductDialogViewModel.setInvoiceProduct(dto = it) },
                        onEditRequest = { dto, index ->
                            createInvoiceProductDialogViewModel.setInvoiceProduct(
                                dto = dto, index = index
                            )
                        },
                        onRemoveRequest = { invoiceProductListViewModel.removeInvoiceProduct(it) },
                    ),
                    createInvoiceProductDialogState = createInvoiceProductDialogState.value,
                    createInvoiceProductDialogCallback = CreateInvoiceProductDialogViewModel.Callback(
                        onPriceChange = {
                            createInvoiceProductDialogViewModel.updatePrice(it)
                        },
                        onCountChange = { createInvoiceProductDialogViewModel.updateCount(it) },
                        onCreateRequest = {
                            invoiceProductListViewModel.addInvoiceProduct(
                                dto = createInvoiceProductDialogViewModel.toDto(),
                                index = createInvoiceProductDialogState.value.index,
                            )
                        },
                        onDismissRequest = { createInvoiceProductDialogViewModel.clear() },
                    ),
                    customerSearchState = TODO(),
                    customerSearchCallback = TODO(),
                    customerSearchResult = TODO(),
                    productSearchState = TODO(),
                    productSearchCallback = TODO(),
                    productSearchResult = TODO(),
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