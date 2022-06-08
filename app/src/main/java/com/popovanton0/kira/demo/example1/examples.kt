package com.popovanton0.kira.demo.example1

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.generated.com.popovanton0.exampleui.Kira_SimpleTextCard
import com.popovanton0.kira.registry.KiraRegistry

@Preview
@Composable
fun KiraProviderExample() = KiraScreen(Kira_SimpleTextCard())

@Preview
@Composable
fun KiraRegistryExample() {
    val (funName, kiraProvider) = KiraRegistry.kiraProviders.entries.first()
    KiraScreen(kiraProvider)
}
