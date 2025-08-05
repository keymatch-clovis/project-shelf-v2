package com.example.project_shelf.framework.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.project_shelf.adapter.view_model.MainViewModel
import com.example.project_shelf.framework.ui.Destination
import com.example.project_shelf.framework.ui.MainDestination
import com.example.project_shelf.framework.ui.MainNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {
    val navController = rememberNavController()
    val startDestination = rememberSaveable { MainDestination.PRODUCT }
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(Unit) {
        // NOTE: We HAVE to execute the initial view model methods from the view. I think this is
        //  expected, as the view model should exist even if the view is not present. Still, I don't
        //  like it that much, I feel the view model should have more responsibility, as this feels
        //  like the view is doing stuff it shouldn't do.
        if (viewModel.shouldLoadDefaultData()) {
            navController.navigate(Destination.LOADING.route)
        }

        viewModel.updateIsLoading(false)
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                NavigationBar {
                    MainDestination.entries.forEach {
                        NavigationBarItem(
                            selected = currentBackStackEntry.value?.destination?.route?.startsWith(
                                it.route
                            ) != false,
                            onClick = {
                                // FIXME: I don't know if this is correct
                                //  I have searched the interwebs for information about how to clear
                                //  _completely_ the back stack, and nothing seems to work as I
                                //  expect it to. This most likely means I'm not doing something
                                //  correctly, but I seem unable to find more information that
                                //  explains otherwise.
                                navController.popBackStack(0, false)

                                navController.navigate(it.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(it.iconVectorResource),
                                    contentDescription = null,
                                )
                            },
                            label = { Text(stringResource(it.labelStringResource)) },
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        MainNavHost(
            mainViewModel = viewModel,
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(contentPadding),
        )
    }
}