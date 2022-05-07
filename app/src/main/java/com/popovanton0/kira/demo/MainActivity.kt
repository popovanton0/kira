package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.prototype1.KiraScreen
import com.popovanton0.kira.prototype1.TextCardParams

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Surface(color = MaterialTheme.colors.background) {
                    //Content()
                    KiraScreen(TextCardParams())
                }
            }
        }
    }
}

@Composable
private fun Content() = Column(
    Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {

}

@Preview
@Composable
public fun TextCard(text: String = "Example", isRed: Boolean = false) {
    com.popovanton0.kira.TextCard()
}