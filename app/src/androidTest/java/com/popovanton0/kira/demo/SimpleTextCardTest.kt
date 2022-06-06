package com.popovanton0.kira.demo

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.Kira_SimpleTextCard
import org.junit.Rule
import org.junit.Test

internal class SimpleTextCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = with(composeTestRule) {
        val kiraProvider = Kira_SimpleTextCard()
        setContent { KiraScreen(kiraProvider) }

        onNodeWithText("isRed").assertExists()
        onNodeWithText("skill").assertExists()
        onAllNodesWithText("Lorem").assertCountEquals(2)
        onAllNodesWithText("null").assertCountEquals(2)
    }
}
