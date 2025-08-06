package com.example.project_shelf.framework.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.project_shelf.R

@Composable
fun AlertDialog(
    headerTextResource: Int,
    onDismissRequest: () -> Unit,
    onAcceptRequest: () -> Unit,
    bodyTextResource: Int? = null,
) {
    // https://m3.material.io/components/dialogs/specs
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            // https://m3.material.io/components/dialogs/specs#9a8c226b-19fa-4d6b-894e-e7d5ca9203e8
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = stringResource(headerTextResource),
                )
                if (bodyTextResource != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = stringResource(bodyTextResource),
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = onAcceptRequest) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}