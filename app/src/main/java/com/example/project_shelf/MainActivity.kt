package com.example.project_shelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.project_shelf.adapter.view_model.MainActivityViewModel
import com.example.project_shelf.framework.datastore.dataStore
import com.example.project_shelf.framework.ui.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>(factoryProducer = {
        viewModelFactory {
            initializer {
                MainActivityViewModel(applicationContext.dataStore)
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            !viewModel.uiState.value.isReady
        }

        setContent {
            val state = viewModel.uiState.collectAsState()
            val navController = rememberNavController()

            if (state.value.isReady) {
                AppNavHost(
                    navController = navController,
                    startDestination = state.value.startDestination,
                )
            }
        }
    }
}