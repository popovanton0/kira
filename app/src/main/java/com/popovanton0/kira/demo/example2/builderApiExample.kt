package com.popovanton0.kira.demo.example2

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.exampleui.Car
import com.popovanton0.exampleui.Engine
import com.popovanton0.exampleui.Food
import com.popovanton0.exampleui.Rock
import com.popovanton0.exampleui.Skill
import com.popovanton0.exampleui.TextCard
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.compound
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.compound.nullableCompound
import com.popovanton0.kira.suppliers.enum
import com.popovanton0.kira.suppliers.kira
import com.popovanton0.kira.suppliers.nullableBoolean
import com.popovanton0.kira.suppliers.nullableEnum
import com.popovanton0.kira.suppliers.singleValue
import com.popovanton0.kira.suppliers.string

@Preview
@Composable
fun KiraBuilderApiExample() = KiraScreen(kiraTextCard)

val kiraTextCard = kira {
    val text = string(paramName = "text", defaultValue = "Lorem")
    val isRed = boolean(paramName = "isRed", defaultValue = false)
    val skill = nullableEnum<Skill?>(paramName = "skill", defaultValue = null)
    val food = enum<Food>(paramName = "food")
    val car = carSupplier()
    val rock = singleValue(
        paramName = "rock",
        typeName = "Rock",
        value = Rock,
    )
    injector {
        TextCard(
            text = text.currentValue(),
            isRed = isRed.currentValue(),
            skill = skill.currentValue(),
            food = food.currentValue(),
            car = car.currentValue(),
            rock = rock.currentValue(),
        )
    }
}

fun KiraScope.carSupplier() = compound(
    paramName = "car default",
    label = "Car"
) {
    val model = string(paramName = "model", defaultValue = "Tesla")
    val lame = boolean(paramName = "lame", defaultValue = false)
    val lameN = nullableBoolean(paramName = "lameN", defaultValue = null)
    val cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
    val engine = nullableCompound(
        paramName = "engine",
        label = "Engine",
        isNullByDefault = true
    ) {
        val model = string(paramName = "model", defaultValue = "Merlin")
        val diesel = boolean(
            paramName = "diesel",
            defaultValue = false
        )
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
            lameN = lameN.currentValue(),
            cookerQuality = cookerQuality.currentValue(),
            engine = engine.currentValue() ?: Engine("null"),
        )
    }
}
