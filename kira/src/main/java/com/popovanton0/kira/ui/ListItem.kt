package com.popovanton0.kira.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Suppress("NAME_SHADOWING")
@Composable
internal fun ListItem(
    modifier: Modifier = Modifier,
    start: (@Composable () -> Unit)? = null,
    overlineText: (@Composable () -> Unit)? = null,
    secondaryText: (@Composable () -> Unit)? = null,
    end: (@Composable () -> Unit)? = null,
    sideSlotsAlignment: Alignment.Vertical = Alignment.CenterVertically,
    text: @Composable () -> Unit,
) {
    val typography = MaterialTheme.typography

    val overlineText = applyTextStyle(typography.overline, ContentAlpha.high, overlineText)
    val text = applyTextStyle(typography.subtitle1, ContentAlpha.high, text)!!
    val secondaryText = applyTextStyle(typography.body2, ContentAlpha.medium, secondaryText)
    val end = applyTextStyle(typography.caption, ContentAlpha.medium, end)

    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = sideSlotsAlignment,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            start?.let { Box { it.invoke() } }
        }
        Column(modifier = Modifier.weight(1.5f)) {
            overlineText?.invoke()
            text.invoke()
            secondaryText?.invoke()
        }
        end?.let { Box(modifier = Modifier.weight(1f), Alignment.CenterEnd) { it.invoke() } }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    contentAlpha: Float,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, content)
        }
    }
}

@Preview
@Composable
private fun Preview1Line() = ListItem(
    start = {
        Icon(
            painter = rememberVectorPainter(Icons.Default.Star),
            contentDescription = null
        )
    },
    end = { Text(text = "End text") },
) {
    Text(text = "Title")
}

@Preview
@Composable
private fun Preview3Lines() = ListItem(
    sideSlotsAlignment = Alignment.Top,
    start = {
        Icon(
            painter = rememberVectorPainter(Icons.Default.Star),
            contentDescription = null
        )
    },
    overlineText = { Text(text = "Overline") },
    secondaryText = { Text(text = "Secondary text") },
    end = { Text(text = "End text") },
) {
    Text(text = "Title")
}


@Preview
@Composable
private fun Preview3MultiLines() = ListItem(
    sideSlotsAlignment = Alignment.Top,
    start = {
        Icon(
            painter = rememberVectorPainter(Icons.Default.Star),
            contentDescription = null
        )
    },
    overlineText = { Text(text = "Overline") },
    secondaryText = { Text(text = "Secondary text") },
    end = { Text(text = "End text") },
) {
    Text(text = "TitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitleTitle")
}
