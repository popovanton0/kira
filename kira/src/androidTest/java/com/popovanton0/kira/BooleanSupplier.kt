package com.popovanton0.kira

import androidx.compose.material.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.compound.root
import com.popovanton0.kira.suppliers.nullableBoolean
import org.junit.Rule
import org.junit.Test

internal class BooleanSupplier {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun booleanSupplier(): Unit = with(composeTestRule) {
        val root = root {
            val isFastCar = boolean("paramName:isFastCar", true)
            injector { Text(text = "car's " + if (isFastCar.currentValue()) "fast" else "slow") }
        }

        setContent { KiraScreen(root = root) }

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
        val root = root {
            val isFastCar = nullableBoolean("paramName:isFastCar", true)
            injector {
                val value = when (isFastCar.currentValue()) {
                    true -> "fast"
                    false -> "slow"
                    null -> "speed is unknown"
                }
                Text(text = "car's $value")
            }
        }

        setContent { KiraScreen(root = root) }

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

/*
/**
         * [boolean] will not be added to the screen because:
         * 1. calling [modify] after initialization is prohibited; initialization is performed in
         * [KiraScreen].
         */
        Assert.assertThrows(Throwable::class.java) {
            root.modify {
                isFastCar = boolean("changed param name", true)
            }
        }
 */
