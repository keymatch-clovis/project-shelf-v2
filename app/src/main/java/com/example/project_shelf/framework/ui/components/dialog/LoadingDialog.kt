package com.example.project_shelf.framework.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.project_shelf.R

@Composable
fun LoadingDialog(
    headlineStringResource: Int,
    bodyStringResource: Int? = null,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Card {
            Column(
                // https://m3.material.io/components/dialogs/specs#9a8c226b-19fa-4d6b-894e-e7d5ca9203e8
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    text = stringResource(headlineStringResource)
                )
                if (bodyStringResource != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        text = stringResource(bodyStringResource)
                    )
                }
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}