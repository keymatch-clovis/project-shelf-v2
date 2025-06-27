package com.example.project_shelf

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.project_shelf.framework.ui.AppNavHost
import com.example.project_shelf.framework.ui.Destination
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore = null

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            AppNavHost(
                navController = navController,
                startDestination = Destination.LOADING,
            )
        }
    }
}