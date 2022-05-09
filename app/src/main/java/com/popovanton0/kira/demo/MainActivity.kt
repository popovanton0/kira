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

public class CarScope : KiraScope() {
    public var model: StringValuesProvider by lateinitVal()
    public var lame: BooleanValuesProvider by lateinitVal()
    public var lameN: NullableBooleanValuesProvider by lateinitVal()
    public var cookerQuality: EnumValuesProvider<Food?> by lateinitVal()
    public var engine: NullableCompositeValuesProvider<Engine, EngineScope> by lateinitVal()
}

public class EngineScope : KiraScope() {
    public var model: StringValuesProvider by lateinitVal()
    public var diesel: BooleanValuesProvider by lateinitVal()
}

public class TextCardScope : KiraScope() {
    public var text: StringValuesProvider by lateinitVal()
    public var isRed: BooleanValuesProvider by lateinitVal()
    public var skill: EnumValuesProvider<Skill?> by lateinitVal()
    public var food: EnumValuesProvider<Food> by lateinitVal()
    public var car: CompositeValuesProvider<Car, CarScope> by lateinitVal()
    public var carN: NullableCompositeValuesProvider<Car, CarScope> by lateinitVal()
    //public var cornerRadius: Dp by lateinitVal()
}

val root = root(
    scope = TextCardScope(),
) {
    text = stringValuesProvider(paramName = "text", defaultValue = "Lorem")
    isRed = boolean(paramName = "isRed", defaultValue = false)
    skill = nullableEnum(paramName = "skill", defaultValue = null)
    food = enum(paramName = "food", defaultValue = Food.GOOD)
    car = compositeValuesProvider(
        scope = CarScope(),
        paramName = "car",
        label = "Car"
    ) {
        carBody()
    }
    carN = nullableCompositeValuesProvider(
        scope = CarScope(),
        paramName = "car",
        label = "Car",
        isNullByDefault = true,
    ) {
        carBody()
    }
    injector {
        TextCard(
            text = text.currentValue(),
            isRed = isRed.currentValue(),
            skill = skill.currentValue(),
            food = food.currentValue(),
            car = car.currentValue(),
            carN = carN.currentValue(),
        )
    }
}

private fun CarScope.carBody(): Injector<Car> {
    model = stringValuesProvider(paramName = "model", defaultValue = "Tesla")
    lame = boolean(paramName = "lame", defaultValue = false)
    lameN = nullableBoolean(paramName = "lame", defaultValue = null)
    cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
    engine = nullableCompositeValuesProvider(
        scope = EngineScope(),
        paramName = "engine",
        label = "Engine",
        isNullByDefault = true
    ) {
        model = stringValuesProvider(paramName = "model", defaultValue = "Merlin")
        diesel = boolean(paramName = "diesel", defaultValue = false)
        injector {
            Engine(
                model = model.currentValue(),
                diesel = diesel.currentValue(),
            )
        }
    }
    return injector {
        Car(
            model = model.currentValue(),
            lame = lame.currentValue(),
            lameN = lame.currentValue(),
            cookerQuality = cookerQuality.currentValue(),
            engine = engine.currentValue() ?: Engine("null"),
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
                        override val valueProviders: List<ValuesProvider<*>> = listOf(root)

                        @Composable
                        override fun invoke() = root.currentValue()
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