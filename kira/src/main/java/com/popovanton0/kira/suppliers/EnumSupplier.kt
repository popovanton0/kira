package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.Dropdown

public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
    defaultValue: T
): EnumSupplierBuilder<T> {
    val enumConstants = enumValues<T>().toMutableList()
    return EnumSupplierBuilder(paramName, defaultValue, enumConstants)
        .also(::addSupplier)
}

public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
): EnumSupplierBuilder<T> {
    val enumConstants = enumValues<T>().toMutableList()
    val defaultValue = enumConstants.firstOrNull() ?: error(
        "Enum class ${T::class.java.name} cannot be instantiated because it has no entries"
    )
    return EnumSupplierBuilder(paramName, defaultValue, enumConstants)
        .also(::addSupplier)
}

public inline fun <reified T : Enum<*>?> KiraScope.nullableEnum(
    paramName: String,
    defaultValue: T
): NullableEnumSupplierBuilder<T> {
    val enumConstants = T::class.java.enumConstants!!
        .toMutableList()
        .apply { add(0, null) }
    return NullableEnumSupplierBuilder(
        paramName = paramName,
        defaultValue = defaultValue,
        enumConstants = enumConstants
    ).also(::addSupplier)
}

public class EnumSupplierBuilder<T : Enum<*>> @PublishedApi internal constructor(
    public var paramName: String,
    public var defaultValue: T,
    public val enumConstants: MutableList<T>,
) : SupplierBuilder<T>() {
    override fun BuildKey.build(): Supplier<T> =
        EnumSupplier(paramName, defaultValue, enumConstants.toList())
}

public class NullableEnumSupplierBuilder<T : Enum<*>?> @PublishedApi internal constructor(
    public var paramName: String,
    public var defaultValue: T?,
    public val enumConstants: MutableList<T?>,
) : SupplierBuilder<T?>() {
    override fun BuildKey.build(): Supplier<T?> =
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
    override fun Ui(params: Any?) {
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

@Preview
@Composable
private fun Preview() =
    KiraScope().enum<AnnotationTarget>("param name").apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() =
    KiraScope().nullableEnum<AnnotationTarget?>("param name", defaultValue = null)
        .apply { initialize() }.Ui()
