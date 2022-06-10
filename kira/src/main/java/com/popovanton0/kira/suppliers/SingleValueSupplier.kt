package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Type
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.base.toClassType
import com.popovanton0.kira.suppliers.compound.KiraScope

@ReflectionUsage
public fun <T : Any> KiraScope.singleValue(
    paramName: String,
    value: NamedValue<T>,
): OneOfManySupplierBuilder<T> =
    singleValue(paramName, value, value::class.toClassType(nullable = false))

@ReflectionUsage
public fun <T : Any> KiraScope.nullableSingleValue(
    paramName: String,
    value: NamedValue<T>,
    isNullByDefault: Boolean = true,
): OneOfManySupplierBuilder<T?> = nullableSingleValue(
    paramName, value, value::class.toClassType(nullable = true), isNullByDefault,
)

public fun <T : Any> KiraScope.singleValue(
    paramName: String,
    value: NamedValue<T>,
    type: Type,
): OneOfManySupplierBuilder<T> = oneOfMany(paramName, listOf(value), type)

public fun <T : Any> KiraScope.nullableSingleValue(
    paramName: String,
    value: NamedValue<T>,
    type: Type,
    isNullByDefault: Boolean = true,
): OneOfManySupplierBuilder<T?> = nullableOneOfMany(
    paramName, listOf(value), type, defaultOptionIndex = if (isNullByDefault) null else 0
)

@Preview
@Composable
private fun Preview() = KiraScope().singleValue(
    "param name", NamedValue("string value"), ClassType("String", ClassType.Variant.CLASS)
).apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableSingleValue(
    "param name", NamedValue("string value"), ClassType("String", ClassType.Variant.CLASS)
).apply { initialize() }.Ui()