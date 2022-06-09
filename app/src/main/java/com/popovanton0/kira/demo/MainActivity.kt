@file:Suppress("IllegalIdentifier")

package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import com.popovanton0.kira.demo.example2.KiraRegistryModificationExample
import com.popovanton0.kira.demo.ui.theme.KiraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Scaffold {
                    KiraRegistryModificationExample()
                    //KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
                    //KiraScreen(`Kira_AsdQ😃∂`())
                }
            }
        }
    }
}

