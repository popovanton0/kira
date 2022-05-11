@file:OptIn(ExperimentalMaterialApi::class)

package com.popovanton0.kira.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

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
        text = { Text(text = label) },
        trailing = {
            Text(text = options[selectedOptionIndex])
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