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
import com.popovanton0.kira.demo.example1.WholeNumbers
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.example1.Kira_WholeNumbersFun
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.example1.Kira_WholeNumbersInDataClass
import com.popovanton0.kira.suppliers.byte
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.dataclass.nullableDataClass
import com.popovanton0.kira.suppliers.nullableByte
import com.popovanton0.kira.suppliers.nullableString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Scaffold {
                    //KiraRegistryModificationExample()
                    //KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
                    //KiraScreen(`Kira_AsdQ😃∂`())
                    KiraScreen(Kira_WholeNumbersInDataClass().kira.modify { generated{ wholeNumbers.defaultValue = WholeNumbers() } })
                    /*KiraScreen(kira {
                        val supp = byte("dsf")//nullableDataClass("my car", Car::class, Car(cookerQuality = Food.EXCELLENT))
                        injector {
                            Text(
                                text = supp.build().currentValue().toString()
                                    .replace(",", ",\n")
                                    .replace("(", "(\n")
                                    .replace(")", ")\n")
                            )
                        }
                    })*/
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