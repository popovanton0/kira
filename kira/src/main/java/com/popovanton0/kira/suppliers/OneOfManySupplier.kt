package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.NamedValue.Companion.withName
import com.popovanton0.kira.suppliers.base.NamedValue
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Type
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.base.toClassType
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.Dropdown

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.oneOfMany(
    paramName: String,
    values: Collection<NamedValue<T>>,
    defaultOptionIndex: Int = 0,
): OneOfManySupplierBuilder<T> = oneOfMany(
    paramName, values, T::class.toClassType(nullable = false), defaultOptionIndex,
)

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.nullableOneOfMany(
    paramName: String,
    values: Collection<NamedValue<T>>,
    defaultOptionIndex: Int = 0,
): OneOfManySupplierBuilder<T?> = nullableOneOfMany(
    paramName, values, T::class.toClassType(nullable = true), defaultOptionIndex,
)

public fun <T> KiraScope.oneOfMany(
    paramName: String,
    values: Collection<NamedValue<T>>,
    type: Type,
    defaultOptionIndex: Int = 0,
): OneOfManySupplierBuilder<T> {
    require(defaultOptionIndex < values.size) { "Index should be less that values.size" }
    return OneOfManySupplierBuilder(paramName, type.notNullable(), values, defaultOptionIndex)
        .also(::addSupplierBuilder)
}

public fun <T : Any> KiraScope.nullableOneOfMany(
    paramName: String,
    values: Collection<NamedValue<T>>,
    type: Type,
    defaultOptionIndex: Int? = null,
): OneOfManySupplierBuilder<T?> {
    if (defaultOptionIndex != null) {
        require(defaultOptionIndex < values.size) { "Index should be less that values.size" }
    }
    return OneOfManySupplierBuilder(
        paramName = paramName, type = type.nullable(),
        values = buildList {
            add(null withName "null")
            addAll(values)
        },
        defaultOptionIndex = defaultOptionIndex?.plus(1) ?: 0,
    ).also(::addSupplierBuilder)
}

public class OneOfManySupplierBuilder<T> internal constructor(
    public var paramName: String,
    public var type: Type,
    public var values: Collection<NamedValue<T>>,
    public var defaultOptionIndex: Int,
) : SupplierBuilder<T>() {
    override fun provideSupplier(): Supplier<T> =
        OneOfManySupplier(paramName, type, values.toList(), defaultOptionIndex)
}

private class OneOfManySupplier<T>(
    private val paramName: String,
    private val type: Type,
    private val values: List<NamedValue<T>>,
    defaultOptionIndex: Int,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(values[defaultOptionIndex].value)
    private var selectedOptionIndex by mutableStateOf(defaultOptionIndex)
    private val displayNames: List<String> = values.map { it.displayName }

    @Composable
    override fun Ui(params: Any?) {
        Dropdown(
            selectedOptionIndex = selectedOptionIndex,
            onSelect = {
                selectedOptionIndex = it
                currentValue = values[it].value
            },
            options = displayNames,
            label = paramName,
            type = type,
        )
    }
}

@Preview
@Composable
private fun Preview() = KiraScope().oneOfMany(
    paramName = "param name",
    type = ClassType("String", ClassType.Variant.CLASS),
    values = AnnotationTarget.values().map { it withName it.name }
).build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableOneOfMany(
    paramName = "param name",
    type = ClassType("String", ClassType.Variant.CLASS),
    values = AnnotationTarget.values().map { it withName it.name }
).build().Ui()
