package com.popovanton0.kira.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.popovanton0.kira.suppliers.base.Type

@Composable
internal fun Dropdown(
    selectedOptionIndex: Int,
    onSelect: (index: Int) -> Unit,
    options: List<String>,
    label: String,
    type: Type,
) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable { expanded = options.size > 1 },
        overlineText = { TypeUi(type) },
        text = { Text(text = label) },
        end = {
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
            Text(text = options[selectedOptionIndex])
        }
    )
}