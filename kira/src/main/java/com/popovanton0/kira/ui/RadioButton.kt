package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
public fun RadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
): Unit = Column(
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    androidx.compose.material.RadioButton(selected = selected, onClick = onClick)
    Text(text = label)
}