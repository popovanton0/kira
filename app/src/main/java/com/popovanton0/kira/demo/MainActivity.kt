package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.registry.KiraRegistry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //KiraRegistryModificationExample()
            KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
        }
    }
}

