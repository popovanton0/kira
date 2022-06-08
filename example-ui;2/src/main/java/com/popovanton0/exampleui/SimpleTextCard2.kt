package com.popovanton0.exampleui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.annotations.Kira

@Kira(name = "SimpleTextCard_Simple_example_ui2")
@Preview
@Composable
fun SimpleTextCard(param2: Boolean = false) =
    Text(text = "SimpleTextCard from example_ui2: param2 = $param2")

@Kira(name = "SimpleTextCard_example_ui2")
@Preview
@Composable
fun SimpleTextCard(
    isRed: Boolean = false,
    text: String = "Example",
) {
    val shape = RoundedCornerShape(12.dp)
    Card(elevation = 8.dp, shape = shape) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SimpleTextCard from example;ui2: $text",
                color = if (isRed) Color.Red else Color.Unspecified
            )
        }
    }
}
