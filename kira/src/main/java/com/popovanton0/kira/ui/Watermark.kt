package com.popovanton0.kira.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun BoxScope.EarlyPreview() = Watermark(text = "Early Preview")

@Composable
internal fun BoxScope.Watermark(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = Color.Red.copy(alpha = 0.8f),
    textColor: Color = Color.White,
    alignment: Alignment = Alignment.TopEnd
) {
    Box(
        modifier = modifier
            .align(alignment)
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Text(text = text, color = textColor)
    }
}
