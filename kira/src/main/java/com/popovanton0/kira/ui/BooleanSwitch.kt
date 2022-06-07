@file:OptIn(ExperimentalMaterialApi::class)

package com.popovanton0.kira.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun BooleanSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
): Unit = ListItem(
    modifier = modifier.clickable { onCheckedChange(!checked) },
    text = { Text(text = label) },
    trailing = {
        Switch(
            modifier = modifier,
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
)

@Composable
internal fun NullableBooleanSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean?,
    onCheckedChange: (Boolean?) -> Unit,
    label: String,
): Unit = ListItem(
    modifier = modifier,
    text = { Text(text = label) },
    trailing = {
        Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RadioButton("null", selected = checked == null) { onCheckedChange(null) }
            RadioButton("false", selected = checked == false) { onCheckedChange(false) }
            RadioButton("true", selected = checked == true) { onCheckedChange(true) }
        }
    }
)