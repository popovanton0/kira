package com.popovanton0.kira.demo

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.generated.com.popovanton0.exampleui.Kira_SimpleTextCard
import org.junit.Rule
import org.junit.Test

internal class SimpleTextCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test(): Unit = with(composeTestRule) {
        val kiraProvider = Kira_SimpleTextCard()
        setContent { KiraScreen(kiraProvider.kira) }

        onNodeWithText("isFast").assertExists()
        onNodeWithText("skill").assertExists()
        onAllNodesWithText("Lorem", substring = true).assertCountEquals(2)
        onAllNodes(hasScrollAction()).onFirst().performTouchInput { swipeUp() }
        onAllNodesWithText("null", substring = true).assertCountEquals(3)
    }
}
