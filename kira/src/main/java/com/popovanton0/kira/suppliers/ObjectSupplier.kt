package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.`object`(
    paramName: String,
): OneOfManySupplierBuilder<T> {
    val kClass = T::class
    val qualifiedName = kClass.qualifiedName!!
    val value = kClass.objectInstance ?: notAnObjectError(qualifiedName)
    return `object`(paramName, qualifiedName, value)
}

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.nullableObject(
    paramName: String,
    isNullByDefault: Boolean = true,
): OneOfManySupplierBuilder<T?> {
    val kClass = T::class
    val qualifiedName = kClass.qualifiedName!!
    val value = kClass.objectInstance ?: notAnObjectError(qualifiedName)
    return nullableObject(paramName, qualifiedName, value, isNullByDefault)
}

@PublishedApi
internal fun notAnObjectError(qualifiedName: String): Nothing =
    error("$qualifiedName is not an object")

public fun <T : Any> KiraScope.`object`(
    paramName: String,
    qualifiedName: String,
    value: T,
): OneOfManySupplierBuilder<T> {
    val type = ClassType(qualifiedName, ClassType.Variant.OBJECT)
    val objectValueName = qualifiedName.substringAfterLast('.')
    return singleValue(paramName, value withName objectValueName, type)
}

public fun <T : Any> KiraScope.nullableObject(
    paramName: String,
    qualifiedName: String,
    value: T,
    isNullByDefault: Boolean = true,
): OneOfManySupplierBuilder<T?> {
    val type = ClassType(qualifiedName, ClassType.Variant.OBJECT)
    val objectValueName = qualifiedName.substringAfterLast('.')
    return nullableSingleValue(paramName, value withName objectValueName, type, isNullByDefault)
}

private object Asd

@Preview
@Composable
private fun Preview() = KiraScope().`object`("param name", "Asd", Asd).apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableObject("param name", "Asd", Asd)
    .apply { initialize() }.Ui()
