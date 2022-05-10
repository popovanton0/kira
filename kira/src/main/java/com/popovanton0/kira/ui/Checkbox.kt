package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
public fun Checkbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
): Unit = Column(
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    androidx.compose.material.Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Text(text = label, fontSize = 12.sp)
}