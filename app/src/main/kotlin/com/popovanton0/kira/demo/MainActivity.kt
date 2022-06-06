package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.Kira_SimpleTextCard
import com.popovanton0.kira.suppliers.KiraProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Preview
@Composable
fun Content() = KiraTheme {
    Surface(color = MaterialTheme.colors.background) {
        KiraScreen(Kira_SimpleTextCard())
        //KiraScreen(kiraTextCard)
    }
}

object KiraRegistry {
    /**
     * key — fully qualified function name
     * value — [KiraProvider] for that key
     */
    public val kiraProviders: MutableMap<String, KiraProvider<*>> =
        HashMap<String, KiraProvider<*>>(0).apply {
            put("com.popovanton0.kira.demo.SimpleTextCard", Kira_SimpleTextCard())
        }
}