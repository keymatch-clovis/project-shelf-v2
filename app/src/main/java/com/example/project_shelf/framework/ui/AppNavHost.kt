package com.example.project_shelf.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_shelf.framework.ui.screen.LoadingScreen
import com.example.project_shelf.framework.ui.screen.MainScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(navController, startDestination = startDestination.path, modifier) {
        composable(Destination.LOADING.path) {
            LoadingScreen(onLoadingDone = {
                // Replace starting route and clear back stack.
                // https://stackoverflow.com/questions/69451490/compose-navigation-replace-starting-route-and-clear-back-stack
                navController.popBackStack(Destination.LOADING.path, true)
                navController.navigate(Destination.MAIN.path)
            })
        }

        composable(Destination.MAIN.path) {
            MainScreen()
        }
    }
}