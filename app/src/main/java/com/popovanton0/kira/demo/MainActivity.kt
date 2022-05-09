package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.prototype1.*
import com.popovanton0.kira.prototype1.valueproviders.*

public class CarScope : KiraScope() {
    public var model: StringSupplierBuilder by lateinitVal()
    public var lame: BooleanSupplierBuilder by lateinitVal()
    public var lameN: NullableBooleanSupplierBuilder by lateinitVal()
    public var cookerQuality: NullableEnumSupplierBuilder<Food?> by lateinitVal()
    public var engine: NullableCompoundSupplierBuilder<Engine, EngineScope> by lateinitVal()
}

public class EngineScope : KiraScope() {
    public var model: StringSupplierBuilder by lateinitVal()
    public var diesel: BooleanSupplierBuilder by lateinitVal()
}

public class TextCardScope : GeneratedKiraScope<TextCardScope.ReassignScope>(ReassignScope()) {
    public class ReassignScope {
        public lateinit var text: SupplierBuilder<String>
        public lateinit var isRed: SupplierBuilder<Boolean>
        public lateinit var skill: SupplierBuilder<Skill?>
        public lateinit var food: SupplierBuilder<Food>
        public lateinit var car: SupplierBuilder<Car>
        public lateinit var carN: SupplierBuilder<Car?>
    }

    public var text: StringSupplierBuilder
        get() = reassignScope.text as StringSupplierBuilder
        set(value) { reassignScope.text = value }
    public var isRed: BooleanSupplierBuilder
        get() = reassignScope.isRed as BooleanSupplierBuilder
        set(value) { reassignScope.isRed = value }
    public var skill: NullableEnumSupplierBuilder<Skill?>
        get() = reassignScope.skill as NullableEnumSupplierBuilder<Skill?>
        set(value) { reassignScope.skill = value }
    public var food: EnumSupplierBuilder<Food>
        get() = reassignScope.food as EnumSupplierBuilder<Food>
        set(value) { reassignScope.food = value }
    public var car: CompoundSupplierBuilder<Car, *>
        get() = reassignScope.car as CompoundSupplierBuilder<Car, *>
        set(value) { reassignScope.car = value }
    public var carN: NullableCompoundSupplierBuilder<Car, *>
        get() = reassignScope.carN as NullableCompoundSupplierBuilder<Car, *>
        set(value) { reassignScope.carN = value }

    override fun collectSuppliers(): List<Supplier<*>> = with(reassignScope) {
        listOf(text, isRed, skill, food, car, carN,)
    }
}

val root = root(TextCardScope()) {
    text = string(paramName = "text", defaultValue = "Lorem")
    isRed = boolean(paramName = "isRed", defaultValue = false)
    skill = nullableEnum(paramName = "skill", defaultValue = null)
    food = enum(paramName = "food", defaultValue = Food.GOOD)
    car = compound(
        scope = CarScope(),
        paramName = "car default",
        label = "Car"
    ) {
        carBody()
    }
    carN = nullableCompound(
        scope = CarScope(),
        paramName = "car",
        label = "Car",
        isNullByDefault = true,
    ) {
        carBody()
    }
    injector {
        DefaultHeader {
            TextCard(
                text = text.currentValue(),
                isRed = isRed.currentValue(),
                skill = skill.currentValue(),
                food = food.currentValue(),
                car = this.car.currentValue(),
                carN = carN.currentValue(),
            )
        }
    }
}

private fun CarScope.carBody(): Injector<Car> {
    model = string(paramName = "model", defaultValue = "Tesla")
    lame = boolean(paramName = "lame", defaultValue = false)
    lameN = nullableBoolean(paramName = "lameN", defaultValue = null)
    cookerQuality = nullableEnum(paramName = "cookerQuality", defaultValue = Food.EXCELLENT)
    engine = nullableCompound(
        scope = EngineScope(),
        paramName = "engine",
        label = "Engine",
        isNullByDefault = true
    ) {
        model = string(paramName = "model", defaultValue = "Merlin")
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
            lameN = lameN.currentValue(),
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
                    //root.scope.car = with(root.scope) {
                    //    compound("car", "Car") {
                    //        injector {
                    //            Car(model = "CUSTOM !!!!")
                    //        }
                    //    }
                    //}
                    KiraScreen(root)
                }
            }
        }
    }
}
