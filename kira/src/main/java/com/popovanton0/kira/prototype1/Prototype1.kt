package com.popovanton0.kira.prototype1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.TextCard
import com.popovanton0.kira.prototype1.valueproviders.*

public interface FunctionParameters<ReturnType> {
    public val valueProviders: List<ValuesProvider<*>>

    @Composable
    public fun invoke(): ReturnType
}

public data class ParameterDetails(
    val name: String,
)

public typealias ValuesProviderProvider<T> = ParameterDetails.() -> ValuesProvider<T>

public enum class Skill { LOW, OK, SICK }
public enum class Food { BAD, GOOD, EXCELLENT }
public data class Engine(
    val model: String = "Merlin",
    val diesel: Boolean = false,
)

public data class Car(
    val model: String = "Tesla",
    val lame: Boolean = false,
    val cookerQuality: Food? = null,
    val engine: Engine = Engine(),
)

public class TextCardValueProviders {
    var text: ValuesProvider<String> = string(defaultValue = "123")
    var isRed: ValuesProvider<Boolean> = boolean(defaultValue = false)
    var textN: ValuesProvider<String?> = nullableString(defaultValue = null)
}

public class TextCardParams(
    text: ValuesProviderProvider<String> = { string(this, defaultValue = "123") },
    isRed: ValuesProviderProvider<Boolean> = { boolean(this, defaultValue = false) },
    textN: ValuesProviderProvider<String?> = { nullableString(this, defaultValue = null) },
    isRedN: ValuesProviderProvider<Boolean?> = { nullableBoolean(this, defaultValue = false) },
    skill: ValuesProviderProvider<Skill?> = { nullableEnum(defaultValue = null) },
    food: ValuesProviderProvider<Food> = { enum(Food.BAD) },
    car: ValuesProviderProvider<Car> = {


        val engine = run {
            val model = string(ParameterDetails("model"), "Merlin")
            val diesel = boolean(ParameterDetails("diesel"), false)

            ParameterDetails("engine").composite(
                label = "Engine",
                model, diesel
            ) {
                Engine(
                    model = model.currentValue(),
                    diesel = diesel.currentValue(),
                )
            }
        }
        CompositeValueProviderBuilder3(
            paramName = "asd",
            label = "Car",
        ) {
            // model, lame, food, engine
            val model = string(ParameterDetails("model"), "Tesla")
            val lame = boolean(ParameterDetails("lame"), false)
            val food = ParameterDetails("cookerQuality").nullableEnum<Food?>(null)
            Injector {
                Car(
                    model = model.currentValue(),
                    lame = lame,
                    cookerQuality = food.currentValue(),
                    engine = engine.currentValue()
                )
            }
        }
        TODO()
    },
    carN: ValuesProviderProvider<Car?> = {
        val model = string(ParameterDetails("model"), "Tesla")
        val lame = boolean(ParameterDetails("lame"), false)
        val food = ParameterDetails("cookerQuality").nullableEnum<Food?>(null)

        nullableComposite(
            label = "Car",
            model, lame, food,
        ) {
            Car(
                model = model.currentValue(),
                lame = lame.currentValue(),
                cookerQuality = food.currentValue(),
            )
        }
    },
    //public val functionCaller: (text: String, isRed: Boolean, textN: String?, isRedN: Boolean?) -> Any = { _, _, _, _ -> },
    public val composableFunctionCaller: @Composable (String, Boolean, String?, Boolean?, Skill?, Food, Car, Car?) -> Unit = { text, isRed, textN, isRedN, skill, food, car, carN, ->
        TextCard(
            text = text,
            isRed = isRed,
            skill = skill,
            food = food,
            car = car,
            carN = carN,
        )
    },
) : FunctionParameters<Unit> {
    public val _text: ValuesProvider<String> = text(ParameterDetails(name = "text"))
    public val _isRed: ValuesProvider<Boolean> = isRed(ParameterDetails(name = "isRed"))
    public val _textN: ValuesProvider<String?> = textN(ParameterDetails(name = "textN"))
    public val _isRedN: ValuesProvider<Boolean?> = isRedN(ParameterDetails(name = "isRedN"))
    public val _skill: ValuesProvider<Skill?> = skill(ParameterDetails(name = "skill"))
    public val _food: ValuesProvider<Food> = food(ParameterDetails(name = "food"))
    public val _car: ValuesProvider<Car> = car(ParameterDetails(name = "car"))
    public val _carN: ValuesProvider<Car?> = carN(ParameterDetails(name = "carN"))

    public override val valueProviders: List<ValuesProvider<*>> = listOf(
        _car, _carN, _text, _isRed, _textN, _isRedN, _skill, _food,
    )

    @Composable
    public override fun invoke(): Unit = composableFunctionCaller(
        _text.currentValue(),
        _isRed.currentValue(),
        _textN.currentValue(),
        _isRedN.currentValue(),
        _skill.currentValue(),
        _food.currentValue(),
        _car.currentValue(),
        _carN.currentValue(),
    )
}

public interface PropertyBasedValuesProvider<T> : ValuesProvider<T> {
    public var currentValue: T

    @Composable
    override fun currentValue(): T = currentValue
}

public interface ValuesProvider<T> {

    @Composable
    public fun currentValue(): T

    @Composable
    public fun Ui()

    /*
    TODO Char

    TODO Byte
    TODO Short
    TODO Int
    TODO Long

    TODO Float
    TODO Double

    TODO String
    */
}

public interface ValueProviderBuilder<T> {
    public fun build(): ValuesProvider<T>
}

public class KiraViewModel<ReturnType>(
    internal val params: FunctionParameters<ReturnType>
) : ViewModel()

@Composable
public fun DefaultHeader(function: @Composable () -> Unit): Unit = Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
            shape = RoundedCornerShape(12.dp)
        )
        .padding(40.dp),
    contentAlignment = Alignment.Center,
) {
    function()
}

@Composable
public fun <ReturnType> KiraScreen(
    params: FunctionParameters<ReturnType>,
    header: @Composable (function: @Composable () -> Unit) -> Unit = { DefaultHeader(it) }
) {
    val vm = viewModel<KiraViewModel<ReturnType>>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            KiraViewModel(params) as T
    })
    val params = vm.params
    Box {
        LazyColumn {
            item { header { params.invoke() } }
            items(params.valueProviders) { it.Ui() }
        }
        EarlyPreview()
    }
}

@Composable
internal fun BoxScope.EarlyPreview() = Watermark(text = "Early Preview")

@Composable
internal fun BoxScope.Watermark(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = Color.Red.copy(alpha = 0.8f),
    textColor: Color = Color.White,
    alignment: Alignment = Alignment.TopEnd
) {
    Box(
        modifier = modifier
            .align(alignment)
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Text(text = text, color = textColor)
    }
}
