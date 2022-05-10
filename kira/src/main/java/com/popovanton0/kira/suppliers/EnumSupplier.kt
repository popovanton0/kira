package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.Dropdown

public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
    defaultValue: T
): EnumSupplierBuilder<T> =
    EnumSupplierBuilder(paramName,defaultValue, T::class.java.enumConstants!!.toMutableList())
        .also(::addSupplier)

public inline fun <reified T : Enum<*>?> KiraScope.nullableEnum(
    paramName: String,
    defaultValue: T
): NullableEnumSupplierBuilder<T> = NullableEnumSupplierBuilder(
    paramName = paramName,
    defaultValue = defaultValue,
    enumConstants = T::class.java.enumConstants!!
        .toMutableList()
        .apply { add(0, null) }
).also(::addSupplier)

public class EnumSupplierBuilder<T : Enum<*>> @PublishedApi internal constructor(
    public var paramName: String,
    public var defaultValue: T,
    public val enumConstants: MutableList<T>,
) : SupplierBuilder<T>() {
    override fun build(key: BuildKey): Supplier<T> =
        EnumSupplier(paramName, defaultValue, enumConstants.toList())
}

public class NullableEnumSupplierBuilder<T : Enum<*>?> @PublishedApi internal constructor(
    public var paramName: String,
    public var defaultValue: T?,
    public val enumConstants: MutableList<T?>,
) : SupplierBuilder<T?>() {
    override fun build(key: BuildKey): Supplier<T?> =
        EnumSupplier(paramName, defaultValue, enumConstants.toList())
}

private class EnumSupplier<T : Enum<*>?>(
    private val paramName: String,
    defaultValue: T,
    /**
     * Sorted by ordinal
     * @see java.lang.Class.getEnumConstants
     * @see Enum.ordinal
     */
    private val enumConstants: List<T>,
) : PropertyBasedSupplier<T> {
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
