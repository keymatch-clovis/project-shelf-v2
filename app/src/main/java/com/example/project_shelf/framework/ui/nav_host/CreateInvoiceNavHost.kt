package com.example.project_shelf.framework.ui.nav_host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_shelf.R
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
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = startDestination.route,
    ) {
        composable(CreateInvoiceDestination.DETAILS.route) {
            CreateInvoiceDetailsForm()
        }

        composable(CreateInvoiceDestination.PRODUCTS.route) {
            CreateInvoiceProductsForm()
        }
    }
}