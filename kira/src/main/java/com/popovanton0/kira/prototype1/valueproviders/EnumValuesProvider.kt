package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.ParameterDetails
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.Dropdown

public inline fun <reified T : Enum<T>> ParameterDetails.enum(defaultValue: T): ValuesProvider<T> =
    NullableEnumValuesProvider(defaultValue, this, T::class.java.enumConstants!!)

public inline fun <reified T : Enum<*>?> ParameterDetails.nullableEnum(
    defaultValue: T?
): ValuesProvider<T?> = NullableEnumValuesProvider(
    defaultValue = defaultValue,
    parameterDetails = this,
    enumConstants = T::class.java.enumConstants!!
        .toMutableList()
        .apply { add(0, null) }
        .toTypedArray()
)

@PublishedApi
internal class NullableEnumValuesProvider<T : Enum<*>?>(
    defaultValue: T,
    private val parameterDetails: ParameterDetails,
    /**
     * Sorted by ordinal
     * @see java.lang.Class.getEnumConstants
     * @see Enum.ordinal
     */
    private val enumConstants: Array<T>,
) : ValuesProvider<T> {
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
            label = parameterDetails.name,
        )
    }
}
