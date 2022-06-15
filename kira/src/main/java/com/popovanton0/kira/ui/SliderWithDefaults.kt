package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SliderWithDefaults(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    isDefault: Boolean,
    onRequestDefault: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Slider(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.1f..1.6f
        )
        Checkbox(
            label = "Default",
            enabled = !isDefault,
            checked = isDefault,
            onCheckedChange = { if (it) onRequestDefault() })
    }
}