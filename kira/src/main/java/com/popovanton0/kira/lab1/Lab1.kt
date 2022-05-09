/*
package com.popovanton0.kira.lab1

import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import com.popovanton0.kira.ui.BooleanSwitch
import kotlinx.coroutines.flow.MutableStateFlow

public interface FunctionParameters {
    public val functionCaller: MutableStateFlow<List<Any>>
    public val params: List<FunctionParameter<*>>
}

public data class ParameterDetails(
    val displayName: String,
)

private fun main() {

    TextCardParams4().copy1 {
        TextCardParams4().copy(isRed = isRed)
    }
}

@Suppress("FunctionName")
public inline fun <T : FunctionParameters> T.copy1(block: T.() -> T): T = block()


public data class TextCardParams4(
    val text: ParameterDetails.() -> Supplier<String> = {

    },
    val isRed: ParameterDetails.() -> Supplier<Boolean> = {
        Supplier.Boolean(this, defaultValue = false)
    },
    val cornerRadius: ParameterDetails.() -> Supplier<Dp> = {

    },
) : FunctionParameters {
    public override val functionCaller: MutableStateFlow<List<Any>> = MutableStateFlow(listOf())
    public override val params: List<FunctionParameter<*>> = TODO()
}

public class TextCardParams3Impl() : TextCardParams3() {
    override val isRed: ParameterDetails.() -> Supplier<Boolean> = super.isRed.copy
}

public abstract class TextCardParams3(
    public open val text: ParameterDetails.() -> Supplier<String> = {

    },
    public open val isRed: ParameterDetails.() -> Supplier<Boolean> = {
        Supplier.Boolean(this, defaultValue = false)
    },
    public open val cornerRadius: ParameterDetails.() -> Supplier<Dp> = {

    },
) : FunctionParameters {
    public override val functionCaller: MutableStateFlow<List<Any>> = MutableStateFlow(listOf())
    public override val params: List<FunctionParameter<*>> = TODO()
}

public abstract class TextCardParams2 : FunctionParameters {
    public open val text: FunctionParameter<String> =
        FunctionParameter("text", Supplier.String, null)
    public open val isRed: FunctionParameter<Boolean> =
        FunctionParameter("isRed", Supplier.Boolean, null)
    public abstract val cornerRadius: FunctionParameter<Dp> // = FunctionParameter("cornerRadius", null, null)

    public override val functionCaller: MutableStateFlow<List<Any>> = MutableStateFlow(listOf())
    public override val params: List<FunctionParameter<*>> by lazy {
        listOf(
            text,
            isRed,
            cornerRadius,
        )
    }
}

public class TextCardParams2Impl : TextCardParams2() {
    override val text: FunctionParameter<String> = super.text.copy(valuesValidator =)
}

public data class FunctionParameter<T>(
    val displayName: String,
    val valuesProvider: Supplier<T>?,
    val valuesValidator: ((Any) -> ValidationResult)?,
) {
    public data class ValidationResult(val valid: Boolean, val reason: String)
}

public interface Supplier<T> {
    public val defaultValue: T
    public val parameterDetails: ParameterDetails
    public var currentValue: T

    @Composable
    public fun Ui()

    public class Boolean(
        override val parameterDetails: ParameterDetails,
        override val defaultValue: kotlin.Boolean,
    ) : Supplier<kotlin.Boolean> {
        public override var currentValue: kotlin.Boolean by mutableStateOf(defaultValue)

        @Composable
        override fun Ui() {
            BooleanSwitch(
                checked = currentValue,
                onCheckedChange = { currentValue = it },
                label = parameterDetails.displayName
            )
        }

    }

    public object Char : Supplier<kotlin.Char>

    public object Byte : Supplier<kotlin.Byte>
    public object Short : Supplier<kotlin.Short>
    public object Int : Supplier<kotlin.Int>
    public object Long : Supplier<kotlin.Long>

    public object Float : Supplier<kotlin.Float>
    public object Double : Supplier<kotlin.Double>

    public object String : Supplier<kotlin.String>

    */
/**
     * Todo rename
     *//*

    public class Enum<T>(
        public val values: List<Value<T>>
    ) : Supplier<T> {
        public sealed class Value<T> {
            public data class Group<T>(
                val displayName: kotlin.String,
                val values: List<Enum.Value<T>>,
            ) : Enum.Value<T>()

            public data class Value<T>(
                val displayName: kotlin.String,
                val value: @Composable () -> T,
            ) : Enum.Value<T>()
        }
    }
}

public class KiraViewModel(
    private val params: FunctionParameters,
) : ViewModel() {
    init {
        Supplier.Enum<Any>(
            listOf(
                Supplier.Enum.Value.Value("dsfds", {}),
                Supplier.Enum.Value.Group(
                    "hfgfg", listOf(
                        Supplier.Enum.Value.Value("dsfds", {}),
                        Supplier.Enum.Value.Value("dsfds", {}),
                        Supplier.Enum.Value.Value("dsfds", {}),
                    )
                ),
            )
        )
    }
}

@Composable
public fun KiraScreen(params: FunctionParameters) {
    params.params.forEach { param ->
        param.valuesProvider.
    }
}*/
