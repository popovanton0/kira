@file:Suppress("IllegalIdentifier")

package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.popovanton0.exampleui.Engine
import com.popovanton0.exampleui.Food
import com.popovanton0.exampleui.Rock
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.demo.example1.WholeNumbers
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.generated.androidx.compose.material.Kira_Checkbox
import com.popovanton0.kira.generated.com.popovanton0.kira.demo.example1.Kira_WholeNumbersInDataClass
import com.popovanton0.kira.suppliers.value

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Scaffold {
                    //KiraRegistryModificationExample()
                    //KiraScreen(KiraRegistry.kiraProviders.filterKeys { it.contains("asd") }.values.first())
                    //KiraScreen(`Kira_AsdQ😃∂`())
                    //KiraBuilderApiExample()
                    if (false)KiraScreen(Kira_WholeNumbersInDataClass().kira.modify {
                        generated {
                            wholeNumbers.defaultValue = WholeNumbers()
                        }
                    })
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
                    //var state by remember { mutableStateOf("") }
                    //KiraScreen(Kira_S {
                    //    Kira_S.Misses(onChanged = value("onChanged") { { state = it } })
                    //}.kira.modify { value = value("value") { state } })
                    var checked by remember { mutableStateOf(false) }
                    KiraScreen(
                        Kira_Checkbox {
                            Kira_Checkbox.Misses(
                                onCheckedChange = value("onCheckedChange") { { checked = it } }
                            )
                        }.kira.modify {
                            this.checked = value("checked") { checked }
                            generated { enabled.defaultValue = true }
                        }
                    )
                }
            }
        }
    }
}

@Kira
@Composable
public fun S(value: String, onChanged: (String) -> Unit) {
    TextField(value = value, onValueChange = onChanged)
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