package com.popovanton0.kira.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.suppliers.base.Type

@Composable
internal fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    paramName: String,
    type: Type,
    errorMsg: String? = null,
    singleLine: Boolean = false,
): Unit = ListItem(
    sideSlotsAlignment = Alignment.Bottom,
    overlineText = { TypeUi(type = type) },
    secondaryText = { ErrorMsg(errorMsg) },
    text = {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = paramName) },
            isError = errorMsg != null,
            singleLine = singleLine,
            maxLines = 4,
        )
    },
)

@Composable
internal fun NullableTextField(
    modifier: Modifier = Modifier,
    value: String?,
    onValueChange: (String?) -> Unit,
    paramName: String,
    type: Type,
    errorMsg: String? = null,
    singleLine: Boolean = false,
) {
    var latestNonNullValue by rememberSaveable { mutableStateOf(value ?: "") }
    ListItem(
        sideSlotsAlignment = Alignment.Bottom,
        overlineText = { TypeUi(type = type) },
        secondaryText = { ErrorMsg(errorMsg) },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f),
                    value = value ?: latestNonNullValue,
                    onValueChange = {
                        latestNonNullValue = it
                        onValueChange(it)
                    },
                    enabled = value != null,
                    label = { Text(text = paramName) },
                    isError = errorMsg != null,
                    singleLine = singleLine,
                    maxLines = 4,
                )
                Checkbox(
                    label = "null",
                    checked = value == null,
                    onCheckedChange = {
                        onValueChange(if (value == null) latestNonNullValue else null)
                    }
                )
            }
        },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ErrorMsg(errorMsg: String?) = AnimatedContent(targetState = errorMsg) {
    if (it != null) Text(
        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        text = it,
        color = MaterialTheme.colors.error
    )
}