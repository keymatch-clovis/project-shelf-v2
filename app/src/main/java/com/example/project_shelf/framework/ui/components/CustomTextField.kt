package com.example.project_shelf.framework.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    errors: List<Int> = emptyList(),
    onClick: (Boolean) -> Unit = {},
    onClear: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier
            .sizeIn(
                // See: `SearchBar.android`.
                minWidth = 360.dp,
                maxWidth = 720.dp,
            )
            .onFocusChanged { if (it.isFocused) onClick(true) },
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        isError = errors.isNotEmpty(),
        singleLine = singleLine,
        readOnly = readOnly,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (required) {
                    Icon(
                        modifier = Modifier.size(11.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.asterisk_solid),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(stringResource(label))
            }
        },
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // TODO: We are just showing the first error, maybe this is not wanted later.
                errors.firstOrNull()?.let {
                    Text(
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = stringResource(it)
                    )
                }
            }
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.xmark_solid),
                        contentDescription = null
                    )
                }
            }
        },
    )
}