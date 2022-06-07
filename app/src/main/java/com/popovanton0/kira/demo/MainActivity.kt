package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.example2.KiraRegistryModificationExample
import com.popovanton0.kira.demo.example2.kiraTextCard
import com.popovanton0.kira.demo.ui.theme.KiraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraRegistryModificationExample()
        }
    }
}

@Preview
@Composable
fun ManualKiraApiUsageExample() = KiraTheme {
    Surface(color = MaterialTheme.colors.background) {
        KiraScreen(kiraTextCard)
    }
}

