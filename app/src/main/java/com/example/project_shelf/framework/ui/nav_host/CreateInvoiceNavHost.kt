package com.example.project_shelf.framework.ui.nav_host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_shelf.R
import com.example.project_shelf.adapter.dto.ui.CustomerFilterDto
import com.example.project_shelf.adapter.dto.ui.InvoiceProductDto
import com.example.project_shelf.adapter.view_model.common.Input
import com.example.project_shelf.framework.ui.components.form.invoice.CreateInvoiceDetailsForm
import com.example.project_shelf.framework.ui.components.form.invoice.CreateInvoiceProductsForm

enum class CreateInvoiceDestination(
    val route: String,
    val labelStringResource: Int,
) {
    DETAILS(
        route = "create.invoice.details",
        labelStringResource = R.string.details,
    ),
    PRODUCTS(
        route = "create.invoice.products",
        labelStringResource = R.string.products,
    ),
}

@Composable
fun CreateInvoiceNavHost(
    modifier: Modifier,
    navHostController: NavHostController,
    startDestination: CreateInvoiceDestination,
    invoiceProducts: List<InvoiceProductDto>,
    customerInput: Input<CustomerFilterDto>,
    onOpenSearchCustomer: () -> Unit,
    onOpenSearchProduct: () -> Unit,
    onEditInvoiceProduct: (InvoiceProductDto) -> Unit,
    onDeleteInvoiceProduct: (InvoiceProductDto) -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = startDestination.route,
    ) {
        composable(CreateInvoiceDestination.DETAILS.route) {
            CreateInvoiceDetailsForm(
                customerInput = customerInput,
                onOpenSearchCustomer = onOpenSearchCustomer,
            )
        }

        composable(CreateInvoiceDestination.PRODUCTS.route) {
            CreateInvoiceProductsForm(
                invoiceProducts = invoiceProducts,
                onOpenSearchProduct = onOpenSearchProduct,
                onEditInvoiceProduct = onEditInvoiceProduct,
                onDeleteInvoiceProduct = onDeleteInvoiceProduct,
            )
        }
    }
}