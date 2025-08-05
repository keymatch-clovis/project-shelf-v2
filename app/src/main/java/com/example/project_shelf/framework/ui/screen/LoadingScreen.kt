package com.example.project_shelf.framework.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.project_shelf.R
import com.example.project_shelf.adapter.view_model.MainViewModel

@Composable()
fun LoadingScreen(
    viewModel: MainViewModel,
) {
    val context = LocalContext.current

    // > Triggering logic != holding logic
    // We need to trigger the data load when this view is loaded.
    LaunchedEffect(Unit) {
        viewModel.loadDefaultData(
            cityData = context.resources.openRawResource(R.raw.departments_cities),
        )
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Text(stringResource(R.string.loading_message))
            CircularProgressIndicator()
        }
    }
}