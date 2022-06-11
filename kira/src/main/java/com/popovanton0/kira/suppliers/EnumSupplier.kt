package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.ClassType.ClassModifier
import com.popovanton0.kira.suppliers.base.NamedValue.Companion.withName
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope

@ReflectionUsage
public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
    defaultValue: T = enumValues<T>().firstOrNull() ?: noEnumInstanceError()
): OneOfManySupplierBuilder<T> = enum(
    paramName, T::class.qualifiedName!!, enumValues(), defaultValue
)

@ReflectionUsage
public inline fun <reified T : Enum<T>> KiraScope.nullableEnum(
    paramName: String,
    defaultValue: T? = null
): OneOfManySupplierBuilder<T?> = nullableEnum(
    paramName, T::class.qualifiedName!!, enumValues(), defaultValue
)

public inline fun <reified T : Enum<T>> KiraScope.enum(
    paramName: String,
    qualifiedName: String,
    defaultValue: T = enumValues<T>().firstOrNull() ?: noEnumInstanceError()
): OneOfManySupplierBuilder<T> = enum(paramName, qualifiedName, enumValues(), defaultValue)

public inline fun <reified T : Enum<T>> KiraScope.nullableEnum(
    paramName: String,
    qualifiedName: String,
    defaultValue: T? = null
): OneOfManySupplierBuilder<T?> = nullableEnum(paramName, qualifiedName, enumValues(), defaultValue)

@PublishedApi
internal fun <T : Enum<T>> KiraScope.enum(
    paramName: String,
    qualifiedName: String,
    values: Array<out T>,
    defaultValue: T = values.firstOrNull() ?: noEnumInstanceError()
): OneOfManySupplierBuilder<T> = oneOfMany(
    paramName = paramName,
    type = enumType(qualifiedName),
    values = values.ifEmpty(::noEnumInstanceError).map { it withName it.name },
    defaultOptionIndex = defaultValue.ordinal
)

@PublishedApi
internal fun <T : Enum<T>> KiraScope.nullableEnum(
    paramName: String,
    qualifiedName: String,
    values: Array<out T>,
    defaultValue: T? = null,
): OneOfManySupplierBuilder<T?> = nullableOneOfMany(
    paramName = paramName,
    type = enumType(qualifiedName),
    values = values.ifEmpty(::noEnumInstanceError).map { it withName it.name },
    defaultOptionIndex = defaultValue?.ordinal
)

@PublishedApi
internal fun noEnumInstanceError(): Nothing = error("Enum class has no instances")

private fun enumType(qualifiedName: String): ClassType = ClassType(
    qualifiedName = qualifiedName,
    variant = ClassType.Variant.CLASS,
    modifiers = setOf(ClassModifier.ENUM),
)

@Preview
@Composable
private fun Preview() =
    KiraScope().enum<AnnotationTarget>("param name", "AnnotationTarget").build().Ui()

@Preview
@Composable
private fun NullablePreview() =
    KiraScope().nullableEnum<AnnotationTarget>("param name", "AnnotationTarget").build().Ui()
