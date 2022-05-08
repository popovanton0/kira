package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.popovanton0.kira.TextCard
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.prototype1.*
import com.popovanton0.kira.prototype1.valueproviders.*

public class CarScope : CompositeValuesProviderScope() {
    public var model: StringValuesProvider by lateinitVal()
    public var lame: BooleanValuesProvider by lateinitVal()
    public var cookerQuality: EnumValuesProvider<Food> by lateinitVal()
    public var engine: CompositeValuesProvider<Engine, EngineScope> by lateinitVal()
}

public class EngineScope : CompositeValuesProviderScope() {
    public var model: StringValuesProvider by lateinitVal()
    public var diesel: BooleanValuesProvider by lateinitVal()
}

val car = compositeValuesProvider(
    scope = CarScope(),
    paramName = "car",
    label = "Car"
) {
    model = stringValuesProvider(paramName = "model", defaultValue = "Tesla")
    lame = booleanValuesProvider(paramName = "lame", defaultValue = false)
    cookerQuality = enum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
    engine = compositeValuesProvider(
        scope = EngineScope(),
        paramName = "engine",
        label = "Engine"
    ) {
        model = stringValuesProvider(paramName = "model", defaultValue = "Merlin")
        diesel = booleanValuesProvider(paramName = "diesel", defaultValue = false)
        injector {
            Engine(
                model = model.currentValue(),
                diesel = diesel.currentValue(),
            )
        }
    }
    injector {
        Car(
            model = model.currentValue(),
            lame = lame.currentValue(),
            cookerQuality = cookerQuality.currentValue(),
            engine = engine.currentValue(),
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Surface(color = MaterialTheme.colors.background) {
                    //Content()

                    val textCard = object : FunctionParameters<Unit> {
                        override val valueProviders: List<ValuesProvider<*>> = listOf(car)

                        @Composable
                        override fun invoke() = TextCard(car = car.currentValue())
                    }
                    KiraScreen(textCard)
                }
            }
        }
    }
}

@Composable
private fun Content() = Column(
    Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {

}