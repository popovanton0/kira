package com.popovanton0.kira.demo.example2

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.exampleui.Car
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.generated.com.popovanton0.exampleui.Kira_TextCard
import com.popovanton0.kira.registry.KiraRegistry
import com.popovanton0.kira.suppliers.base.NamedValue.Companion.withName
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.nullableSingleValue
import com.popovanton0.kira.suppliers.singleValue

@OptIn(ReflectionUsage::class)
@Preview
@Composable
fun KiraRegistryModificationExample() {
    val kiraProviders = KiraRegistry.kiraProviders
    kiraProviders["com.popovanton0.kira.demo.example2.TextCard"] = Kira_TextCard()

    // in the browser UI logic, in other file
    val kiraProvider = kiraProviders["com.popovanton0.kira.demo.example2.TextCard"]!!
    KiraScreen(kiraProvider)
}
