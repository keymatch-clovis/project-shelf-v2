package com.example.project_shelf.framework.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R

@Composable
fun CustomTextField(
    value: String,
    label: Int,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null,
    required: Boolean = false,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    errors: List<Int> = emptyList(),
    onClick: (Boolean) -> Unit = {},
    onClear: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    // FIXME:
    //  I couldn't find any implementation of the dirty input feature, so this may (must) be very
    //  wrong, as I'm using 2 variables per input field just for this. It might not be very good,
    //  but it stays like this for now.
    var wasFocused by remember { mutableStateOf(false) }
    var isDirty by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .sizeIn(
                // See: `SearchBar.android`.
                minWidth = 360.dp,
                maxWidth = 720.dp,
            )
            .onFocusChanged {
                if (it.isFocused) {
                    wasFocused = true
                    onClick(true)
                }
                // If the user focuses, and then un-focuses, we can mark the text field as
                // dirty.
                if (!it.isFocused && wasFocused) {
                    wasFocused = false
                    isDirty = true
                }
            },
        visualTransformation = visualTransformation,
        value = value,
        onValueChange = {
            // Also, if the user starts typing, we have to mark the text field as dirty, so the
            // other possible errors are checked.
            isDirty = true

            onValueChange?.invoke(it)
        },
        keyboardOptions = keyboardOptions,
        isError = isDirty && errors.isNotEmpty(),
        singleLine = singleLine,
        readOnly = readOnly,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (required) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.asterisk),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text(stringResource(label))
            }
        },
        supportingText = {
            if (isDirty) {
                errors.firstOrNull()?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // TODO: We are just showing the first error, maybe this is not wanted later.
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(it)
                        )
                    }
                }
            }
        },
        trailingIcon = {
            if (value.isNotEmpty() && onClear != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.x),
                        contentDescription = null,
                    )
                }
            }

            if (value.isNotEmpty() && readOnly) {
                // TODO: do this
                IconButton(onClick = {}) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.clipboard_copy),
                        contentDescription = null,
                    )
                }
            }
        },
    )
}