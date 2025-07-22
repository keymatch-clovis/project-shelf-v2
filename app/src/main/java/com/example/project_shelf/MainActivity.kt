package com.example.project_shelf

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project_shelf.adapter.view_model.MainViewModel
import com.example.project_shelf.framework.datastore.dataStore
import com.example.project_shelf.framework.ui.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>(factoryProducer = {
        viewModelFactory {
            initializer {
                MainViewModel(applicationContext.dataStore)
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        // Keep the splash screen showing while we do initialization steps. These initialization
        // steps are not done here, they are done later in the main screen. This is so we can use
        // the information to navigate to the correct route, but this has to be done with the
        // navigation controller.
        lifecycleScope.launch {
            splashScreen.setKeepOnScreenCondition { !viewModel.isAppReady.value }
        }

        setContent {
            MainScreen(mainViewModel = viewModel)
        }
    }
}