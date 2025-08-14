package com.example.project_shelf.framework.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomDialog(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    onDismissRequest: () -> Unit,
    component: @Composable () -> Unit,
) {
    // https://m3.material.io/components/dialogs/specs
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // https://m3.material.io/components/dialogs/specs#9a8c226b-19fa-4d6b-894e-e7d5ca9203e8
            Column(modifier = Modifier.padding(24.dp)) {
                component()
            }
        }
    }
}