@file:OptIn(ExperimentalMaterialApi::class)

package com.popovanton0.kira.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
public fun BooleanSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
): Unit = ListItem(
    modifier = modifier,
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
public fun NullableBooleanSwitch(
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

@Composable
private fun RadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) = Column(
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    RadioButton(selected = selected, onClick = onClick)
    Text(text = label)
}

@Composable
public fun TextField(
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
public fun NullableTextField(
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
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = value == null,
                    onCheckedChange = {
                        onValueChange(if (value == null) latestNonNullValue else null)
                    }
                )
                Text(text = "Null")
            }
        }
    )
}

@Composable
public fun Dropdown(
    selectedOptionIndex: Int,
    onSelect: (index: Int) -> Unit,
    options: List<String>,
    label: String,
) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable { expanded = true },
        secondaryText = { Text(text = options[selectedOptionIndex]) },
        text = { Text(text = label) },
        trailing = {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(onClick = { onSelect(index); expanded = false }) {
                        Text(option)
                    }
                }
            }
        }
    )
}
