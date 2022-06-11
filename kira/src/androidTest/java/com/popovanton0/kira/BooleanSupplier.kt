package com.popovanton0.kira

import androidx.compose.material.Text
import androidx.compose.ui.test.isNotSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.nullableBoolean
import org.junit.Rule
import org.junit.Test

internal class BooleanSupplier {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun booleanSupplier(): Unit = with(composeTestRule) {
        val root = kira {
            val isFastCar = boolean("paramName:isFastCar", true)
            injector {
                Text(text = "car's " + if (isFastCar.build().currentValue()) "fast" else "slow")
            }
        }

        setContent { KiraScreen(root) }

        onNodeWithText("car's fast")
            .assertExists("UI was not displayed or default value is wrong")

        onNodeWithText("paramName:isFastCar")
            .assertExists("BooleanProvider's UI is not found")
            .performClick()

        onNodeWithText("car's slow")
            .assertExists("Value was not changed to 'false'")
    }

    @Test
    fun nullableBooleanSupplier(): Unit = with(composeTestRule) {
        val root = kira {
            val isFastCar = nullableBoolean("paramName:isFastCar", true)
            injector {
                val value = when (isFastCar.build().currentValue()) {
                    true -> "fast"
                    false -> "slow"
                    null -> "speed is unknown"
                }
                Text(text = "car's $value")
            }
        }

        setContent { KiraScreen(root) }

        onNodeWithText("car's fast")
            .assertExists("UI was not displayed or default value is wrong")

        onNodeWithText("paramName:isFastCar")
            .assertExists("BooleanProvider's UI is not found")

        onAllNodes(isNotSelected()).onLast().performClick() // false

        onNodeWithText("car's slow")
            .assertExists("Value was not changed to 'false'")

        onAllNodes(isNotSelected()).onFirst().performClick() // null

        onNodeWithText("car's speed is unknown")
            .assertExists("Value was not changed to 'null'")
    }
}
