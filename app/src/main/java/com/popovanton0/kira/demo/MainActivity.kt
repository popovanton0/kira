@file:Suppress("IllegalIdentifier")

package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import com.popovanton0.exampleui.Engine
import com.popovanton0.exampleui.Food
import com.popovanton0.exampleui.Rock
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.dataclass.nullableDataClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Scaffold {
                    //KiraRegistryModificationExample()
                    //KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
                    //KiraScreen(`Kira_AsdQ😃∂`())
                    KiraScreen(kira {
                        val supp = nullableDataClass("my car", Car::class, Car(cookerQuality = Food.EXCELLENT))
                        injector {
                            Text(
                                text = supp.build().currentValue().toString()
                                    .replace(",", ",\n")
                                    .replace("(", "(\n")
                                    .replace(")", ")\n")
                            )
                        }
                    })
                }
            }
        }
    }
}

data class Car(
//    val car: com.popovanton0.kira.demo.Car,
    val rock: Rock? = null,
    val model: String? = "Tesla",
    val lame: Boolean = true,
    val lameN: Boolean? = true,
    val cookerQuality: Food,
    val engine: Engine = Engine(model = "Custom Eng Model", diesel = true),
)