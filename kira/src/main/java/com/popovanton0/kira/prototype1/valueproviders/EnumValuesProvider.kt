package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.PropertyBasedValuesProvider
import com.popovanton0.kira.ui.Dropdown

public inline fun <reified T : Enum<T>> enum(
    paramName: String,
    defaultValue: T
): EnumValuesProvider<T> =
    EnumValuesProvider(defaultValue, paramName, T::class.java.enumConstants!!)

public inline fun <reified T : Enum<*>?> nullableEnum(
    paramName: String,
    defaultValue: T?
): EnumValuesProvider<T?> = EnumValuesProvider(
    defaultValue = defaultValue,
    paramName = paramName,
    enumConstants = T::class.java.enumConstants!!
        .toMutableList()
        .apply { add(0, null) }
        .toTypedArray()
)
public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
    defaultValue: T
): EnumValuesProvider<T> =
    EnumValuesProvider(defaultValue, paramName, T::class.java.enumConstants!!)
        .also(::addValuesProvider)

public inline fun <reified T : Enum<*>?> KiraScope.nullableEnum(
    paramName: String,
    defaultValue: T?
): EnumValuesProvider<T?> = EnumValuesProvider(
    defaultValue = defaultValue,
    paramName = paramName,
    enumConstants = T::class.java.enumConstants!!
        .toMutableList()
        .apply { add(0, null) }
        .toTypedArray()
).also(::addValuesProvider)

public class EnumValuesProvider<T : Enum<*>?> @PublishedApi internal constructor(
    defaultValue: T,
    private val paramName: String,
    /**
     * Sorted by ordinal
     * @see java.lang.Class.getEnumConstants
     * @see Enum.ordinal
     */
    private val enumConstants: Array<T>,
) : PropertyBasedValuesProvider<T> {
    private val enumConstantNames: List<String> = enumConstants.map { it?.name ?: "null" }
    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() {
        Dropdown(
            selectedOptionIndex = run {
                if (enumConstants.first() == null) currentValue?.ordinal?.plus(1) ?: 0
                else currentValue!!.ordinal
            },
            onSelect = { currentValue = enumConstants[it] },
            options = enumConstantNames,
            label = paramName,
        )
    }
}
