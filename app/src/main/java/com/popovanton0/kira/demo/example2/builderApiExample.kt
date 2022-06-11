package com.popovanton0.kira.demo.example2

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.exampleui.Car
import com.popovanton0.exampleui.Engine
import com.popovanton0.exampleui.Food
import com.popovanton0.exampleui.Rock
import com.popovanton0.exampleui.Skill
import com.popovanton0.exampleui.TextCard
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.suppliers.base.NamedValue.Companion.withName
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.toClassType
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

@OptIn(ReflectionUsage::class)
val kiraTextCard = kira {
    val text = string(paramName = "text", defaultValue = "Lorem")
    val isRed = boolean(paramName = "isRed")
    val skill = nullableEnum<Skill>(paramName = "skill")
    val food = enum<Food>(paramName = "food")
    val car = carSupplier()
    val rock = singleValue(
        paramName = "rock",
        type = Rock::class.toClassType(),
        value = Rock withName "Rock",
    )
    injector {
        TextCard(
            text = text.build().currentValue(),
            isRed = isRed.build().currentValue(),
            skill = skill.build().currentValue(),
            food = food.build().currentValue(),
            car = car.build().currentValue(),
            rock = rock.build().currentValue(),
        )
    }
}

@OptIn(ReflectionUsage::class)
fun KiraScope.carSupplier() = compound(
    paramName = "car default",
    type = Car::class.toClassType()
) {
    val model = string(paramName = "model", defaultValue = "Tesla")
    val lame = boolean(paramName = "lame", defaultValue = false)
    val lameN = nullableBoolean(paramName = "lameN", defaultValue = null)
    val cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
    val engine = nullableCompound(
        paramName = "engine",
        type = Engine::class.toClassType(),
        isNullByDefault = true
    ) {
        val model = string(paramName = "model", defaultValue = "Merlin")
        val car = compound(
            paramName = "inner car",
            type = Car::class.toClassType()
        ) {
            val model = string(paramName = "model", defaultValue = "Tesla")
            val lame = boolean(paramName = "lame", defaultValue = false)
            val lameN = nullableBoolean(paramName = "lameN", defaultValue = null)
            val cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
            val engine = nullableCompound(
                paramName = "engine",
                type = Engine::class.toClassType(),
                isNullByDefault = true
            ) {
                val model = string(paramName = "model", defaultValue = "Merlin")
                val diesel = boolean(
                    paramName = "diesel",
                    defaultValue = false
                )
                val carStr = nullableCompound(
                    paramName = "carStr",
                    type = Car::class.toClassType(),
                    isNullByDefault = true
                ) {
                    val model = string(paramName = "model", defaultValue = "Merlin")
                    val car = compound(
                        paramName = "inner car",
                        type = Car::class.toClassType(),
                    ) {
                        val model = string(paramName = "model", defaultValue = "Tesla")
                        val lame = boolean(paramName = "lame", defaultValue = false)
                        val lameN = nullableBoolean(paramName = "lameN")
                        val cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
                        val engine = nullableCompound(
                            paramName = "engine",
                            type = Engine::class.toClassType(),
                            isNullByDefault = true
                        ) {
                            val model = string(paramName = "model", defaultValue = "Merlin")
                            val diesel = boolean(
                                paramName = "diesel",
                                defaultValue = false
                            )
                            injector {
                                Engine(
                                    model = model.build().currentValue(),
                                    diesel = diesel.build().currentValue(),
                                )
                            }
                        }
                        injector {
                            Car(
                                model = model.build().currentValue(),
                                lame = lame.build().currentValue(),
                                lameN = lameN.build().currentValue(),
                                cookerQuality = cookerQuality.build().currentValue(),
                                engine = engine.build().currentValue() ?: Engine("null"),
                            )
                        }
                    }
                    injector {
                        car.build().currentValue().toString()
                    }
                }
                injector {
                    Engine(
                        model = model.build().currentValue() + carStr.build().currentValue(),
                        diesel = diesel.build().currentValue(),
                    )
                }
            }
            injector {
                Car(
                    model = model.build().currentValue(),
                    lame = lame.build().currentValue(),
                    lameN = lameN.build().currentValue(),
                    cookerQuality = cookerQuality.build().currentValue(),
                    engine = engine.build().currentValue() ?: Engine("null"),
                )
            }
        }
        val diesel = boolean(
            paramName = "diesel",
            defaultValue = false
        )
        injector {
            Engine(
                model = car.build().currentValue().toString(),
                diesel = diesel.build().currentValue(),
            )
        }
    }
    injector {
        Text(text = "This text will not be shown")
        Car(
            model = model.build().currentValue(),
            lame = lame.build().currentValue(),
            lameN = lameN.build().currentValue(),
            cookerQuality = cookerQuality.build().currentValue(),
            engine = engine.build().currentValue() ?: Engine("null"),
        )
    }
}
