@file:OptIn(ExperimentalMaterialApi::class)

package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
internal fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
): Unit = ListItem {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) }
    )
}

@Composable
internal fun NullableTextField(
    modifier: Modifier = Modifier,
    value: String?,
    onValueChange: (String?) -> Unit,
    label: String,
) {
    var latestNonNullValue by remember { mutableStateOf("") }
    ListItem(
        text = {
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                value = value ?: latestNonNullValue,
                onValueChange = {
                    latestNonNullValue = it
                    onValueChange(it)
                },
                enabled = value != null,
                label = { Text(text = label) }
            )
        },
        trailing = {
            Checkbox(
                label = "null",
                checked = value == null,
                onCheckedChange = { onValueChange(if (value == null) latestNonNullValue else null) }
            )
        }
    )
}