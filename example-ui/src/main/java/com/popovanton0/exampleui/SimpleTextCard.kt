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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popovanton0.kira.annotations.Kira

@Kira(name = "SimpleTextCard_Simple")
@Preview
@Composable
fun SimpleTextCard(param1: Boolean = false) = Text(text = "SimpleTextCard: param1 = $param1")

@Kira
@Preview
@Composable
fun SimpleTextCard(
    text: String? = "Example",
    isFast: Boolean = true,
    skill: Skill? = Skill.LOW,
    food: Food = Food.BAD,
    rock: Rock? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(elevation = 8.dp, shape = shape) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text.toString())
            Text(
                text = "SimpleTextCard is: ${if (isFast) "🏎" else "🐢"}",
            )
            Text(
                text = "skill is " + when (skill) {
                    Skill.LOW -> "😤"
                    Skill.OK -> "🙂"
                    Skill.SICK -> "😎"
                    null -> "missing"
                },
                fontSize = 18.sp,
            )
            Text(
                text = "🍔 is " + when (food) {
                    Food.BAD -> "😤"
                    Food.GOOD -> "🙂"
                    Food.EXCELLENT -> "😃"
                },
                fontSize = 18.sp,
            )
            Text(text = "rock: ${if (rock != null) "🪨" else null}")
        }
    }
}
