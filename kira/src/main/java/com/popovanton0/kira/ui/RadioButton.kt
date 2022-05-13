package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
public fun RadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
): Unit = Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {
    androidx.compose.material.RadioButton(selected = selected, onClick = onClick)
    Text(text = label)
}