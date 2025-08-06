package com.example.project_shelf.framework.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import kotlinx.coroutines.launch

/**
 * Used to show if the saved status of the invoice draft.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftIndicator(
    loading: Boolean,
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        state = tooltipState,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                if (loading) {
                    Text(stringResource(R.string.invoice_draft_save_loading))
                } else {
                    Text(stringResource(R.string.invoice_draft_save_success))
                }
            }
        },
    ) {
        if (loading) {
            IconButton(
                onClick = { coroutineScope.launch { tooltipState.show() } },
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        } else {
            IconButton(
                onClick = { coroutineScope.launch { tooltipState.show() } },
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.shield_check),
                    contentDescription = null,
                )
            }
        }
    }
}