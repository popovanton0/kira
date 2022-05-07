package com.popovanton0.kira.prototype1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.TextCard
import com.popovanton0.kira.prototype1.valueproviders.*

public interface FunctionParameters<ReturnType> {
    public val valueProviders: List<ValuesProvider<*>>
    public fun invoke(): ReturnType
}

public interface ComposableFunctionParameters<ReturnType> {
    public val valueProviders: List<ValuesProvider<*>>

    @Composable
    public fun ComposableInvoke(): ReturnType
}

public data class ParameterDetails(
    val name: String,
)

public typealias ValuesProviderProvider<T> = ParameterDetails.() -> ValuesProvider<T>

public enum class Skill { LOW, OK, SICK }
public enum class Food { BAD, GOOD, EXCELLENT }

public class TextCardParams(
    text: ValuesProviderProvider<String> = { string("123") },
    isRed: ValuesProviderProvider<Boolean> = { boolean(defaultValue = false) },
    textN: ValuesProviderProvider<String?> = { nullableString(defaultValue = null) },
    isRedN: ValuesProviderProvider<Boolean?> = { nullableBoolean(defaultValue = false) },
    skill: ValuesProviderProvider<Skill?> = { nullableEnum(defaultValue = null) },
    food: ValuesProviderProvider<Food> = { enum(Food.BAD) },
    //public val functionCaller: (text: String, isRed: Boolean, textN: String?, isRedN: Boolean?) -> Any = { _, _, _, _ -> },
    public val composableFunctionCaller: @Composable (String, Boolean, String?, Boolean?, Skill?, Food) -> Unit = { text, isRed, textN, isRedN, skill, food ->
        TextCard(
            text = text,
            isRed = isRed,
            skill = skill,
            food = food,
        )
    },
) : ComposableFunctionParameters<Unit> {
    public val _text: ValuesProvider<String> = text(ParameterDetails(name = "text"))
    public val _isRed: ValuesProvider<Boolean> = isRed(ParameterDetails(name = "isRed"))
    public val _textN: ValuesProvider<String?> = textN(ParameterDetails(name = "textN"))
    public val _isRedN: ValuesProvider<Boolean?> = isRedN(ParameterDetails(name = "isRedN"))
    public val _skill: ValuesProvider<Skill?> = skill(ParameterDetails(name = "skill"))
    public val _food: ValuesProvider<Food> = food(ParameterDetails(name = "food"))

    public override val valueProviders: List<ValuesProvider<*>> = listOf(
        _text, _isRed, _textN, _isRedN, _skill, _food,
    )

    // either
    //public fun invoke(): Any = functionCaller(
    //    _text.currentValue,
    //    _isRed.currentValue,
    //    _textN.currentValue,
    //    _isRedN.currentValue,
    //)

    // or
    @Composable
    public override fun ComposableInvoke(): Unit = composableFunctionCaller(
        _text.currentValue,
        _isRed.currentValue,
        _textN.currentValue,
        _isRedN.currentValue,
        _skill.currentValue,
        _food.currentValue,
    )
}

/**
 * Todo rename
 */
/*public class Enum<T>(
    public val values: List<Value<T>>
) : ValuesProvider<T> {
    public sealed class Value<T> {
        public data class Group<T>(
            val displayName: String,
            val values: List<Enum.Value<T>>,
        ) : Enum.Value<T>()

        public data class Value<T>(
            val displayName: String,
            val value: @Composable () -> T,
        ) : Enum.Value<T>()
    }
}*/

public interface ValuesProvider<T> {
    public var currentValue: T

    @Composable
    public fun Ui()

    /*public object Char : ValuesProvider<kotlin.Char>

    public object Byte : ValuesProvider<kotlin.Byte>
    public object Short : ValuesProvider<kotlin.Short>
    public object Int : ValuesProvider<kotlin.Int>
    public object Long : ValuesProvider<kotlin.Long>

    public object Float : ValuesProvider<kotlin.Float>
    public object Double : ValuesProvider<kotlin.Double>

    public object String : ValuesProvider<kotlin.String>*/
}

@Composable
public fun <ReturnType> KiraScreen(
    params: ComposableFunctionParameters<ReturnType>, header: @Composable () -> Unit = {
        Box(modifier = Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
            params.ComposableInvoke()
        }
    }
) {
    LazyColumn {
        item { header() }
        items(params.valueProviders) { it.Ui() }
    }
}