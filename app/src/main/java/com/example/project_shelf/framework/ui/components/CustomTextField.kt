package com.example.project_shelf.framework.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project_shelf.R
import com.example.project_shelf.adapter.ViewModelError
import com.example.project_shelf.framework.ui.getStringResource

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    singleLine: Boolean = true,
    errors: List<ViewModelError> = emptyList(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    var isDirty by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = {
            isDirty = true
            onValueChange(it)
        },
        keyboardOptions = keyboardOptions,
        isError = isDirty && errors.isNotEmpty(),
        singleLine = singleLine,
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
                if (isDirty) {
                    errors.firstOrNull()?.let {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = stringResource(it.getStringResource())
                        )
                    }
                }
            }
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
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