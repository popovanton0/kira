package com.popovanton0.kira.demo.example2

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.exampleui.Car
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.example2.Kira_TextCard
import com.popovanton0.kira.generated.registry.KiraRegistry
import com.popovanton0.kira.suppliers.nullableSingleValue
import com.popovanton0.kira.suppliers.singleValue

@Preview
@Composable
fun KiraRegistryModificationExample() = KiraTheme {
    Surface(color = MaterialTheme.colors.background) {
        val kiraProviders = KiraRegistry.kiraProviders
        kiraProviders["com.popovanton0.kira.demo.example2.TextCard"] = Kira_TextCard(
            missesProvider = {
                Kira_TextCard.Misses(
                    car = carSupplier(),
                    carN = nullableSingleValue("carN", Car(), nullByDefault = true),
                    autoCar = singleValue("autoCar", Car()),
                )
            }
        )

        // in the browser UI logic, in other file
        val kiraProvider = kiraProviders["com.popovanton0.kira.demo.example2.TextCard"]!!
        KiraScreen(kiraProvider)
    }
}
