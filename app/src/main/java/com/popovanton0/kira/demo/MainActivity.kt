package com.popovanton0.kira.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.KiraScreen
import com.popovanton0.kira.demo.ui.theme.KiraTheme
import com.popovanton0.kira.lateinitVal
import com.popovanton0.kira.suppliers.*
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.*
import com.popovanton0.kira.ui.DefaultHeader

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

public class TextCardScope : GeneratedKiraScopeWithImpls<TextCardScope.SupplierImplsScope>() {
    override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)

    public class SupplierImplsScope(private val scope: TextCardScope): GeneratedKiraScopeWithImpls.SupplierImplsScope() {
        public var text: StringSupplierBuilder
            get() = scope.text as? StringSupplierBuilder ?: implChanged()
            set(value) { scope.text = value }

        public var isRed: BooleanSupplierBuilder
            get() = scope.isRed as? BooleanSupplierBuilder ?: implChanged()
            set(value) { scope.isRed = value }

        public var skill: NullableEnumSupplierBuilder<Skill?>
            get() = scope.skill as? NullableEnumSupplierBuilder<Skill?> ?: implChanged()
            set(value) { scope.skill = value }

        public var food: EnumSupplierBuilder<Food>
            get() = scope.food as? EnumSupplierBuilder<Food> ?: implChanged()
            set(value) { scope.food = value }

        public var car: CompoundSupplierBuilder<Car, *>
            get() = scope.car as? CompoundSupplierBuilder<Car, *> ?: implChanged()
            set(value) { scope.car = value }

        public var carN: NullableCompoundSupplierBuilder<Car, *>
            get() = scope.carN as? NullableCompoundSupplierBuilder<Car, *> ?: implChanged()
            set(value) { scope.carN = value }

    }

    public lateinit var text: Supplier<String>
    public lateinit var isRed: Supplier<Boolean>
    public lateinit var skill: Supplier<Skill?>
    public lateinit var food: Supplier<Food>
    public lateinit var car: Supplier<Car>
    public lateinit var carN: Supplier<Car?>
    public lateinit var rock: Supplier<Rock?>
    public lateinit var oak: Supplier<Oak>

    override fun collectSuppliers(): List<Supplier<*>> =
        listOf(text, isRed, skill, food, car, carN, rock)
}

public interface Oak
public object Rock

fun textCartRoot(/*oak: Supplier<Oak>, injector: TextCardScope.() -> Injector<Unit>*/) = root(TextCardScope()) {
    text = string(paramName = "text", defaultValue = "Lorem")
    isRed = boolean(paramName = "isRed", defaultValue = false)
    skill = nullableEnum(paramName = "skill", defaultValue = null)
    food = enum(paramName = "food")
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
    rock = nullableCompound<Rock>(
        paramName = "rock",
        label = "Rock",
        isNullByDefault = true,
    ) {
        injector {
            Rock
        }
    }/*
    rock = singleValue(
        paramName = "rock",
        typeName = "Rock",
        value = Rock,
    )*/
    injector {
        DefaultHeader {
            TextCard(
                text = text.currentValue(),
                isRed = isRed.currentValue(),
                skill = skill.currentValue(),
                food = food.currentValue(),
                car = car.currentValue(),
                carN = carN.currentValue(),
                rock = rock.currentValue(),
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

////////////////////////////////////////////////////////////////
//////    All of the code above will be auto-generated  ////////
////////////////////////////////////////////////////////////////

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraTheme {
                Surface(color = MaterialTheme.colors.background) {
                    /*root.modify {
                        generatedSupplierImpls { isRed.paramName = "isRed" }
                        food = compound(
                            paramName = "text compound",
                            label = "String"
                        ) {
                            val s = enum(paramName = "text", defaultValue = Food.BAD)
                            injector { s.currentValue() }
                        }
                    }*/
                    KiraScreen(textCartRoot())
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() = KiraTheme {
    Surface(color = MaterialTheme.colors.background) {
        //KiraScreen(root)
    }
}