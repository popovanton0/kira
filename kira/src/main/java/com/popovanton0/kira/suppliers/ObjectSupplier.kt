package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.NamedValue.Companion.withName
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.dataclass.DataClassSupplierSupport
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.`object`(
    paramName: String,
): ObjectSupplierBuilder<T> {
    val kClass = T::class
    val qualifiedName = kClass.qualifiedName!!
    val value = kClass.objectInstance ?: notAnObjectError(qualifiedName)
    return `object`(paramName, qualifiedName, value)
}

@ReflectionUsage
public inline fun <reified T : Any> KiraScope.nullableObject(
    paramName: String,
    isNullByDefault: Boolean = true,
): NullableObjectSupplierBuilder<T> {
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
): ObjectSupplierBuilder<T> =
    ObjectSupplierBuilder(paramName, qualifiedName, value).also(::addSupplierBuilder)

public fun <T : Any> KiraScope.nullableObject(
    paramName: String,
    qualifiedName: String,
    value: T,
    isNullByDefault: Boolean = true,
): NullableObjectSupplierBuilder<T> =
    NullableObjectSupplierBuilder(paramName, qualifiedName, value, isNullByDefault)
        .also(::addSupplierBuilder)

public class ObjectSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public var qualifiedName: String,
    public val value: T,
) : SupplierBuilder<T>() {
    override fun provideSupplier(): Supplier<T> {
        val type = ClassType(qualifiedName, ClassType.Variant.OBJECT)
        val objectValueName = qualifiedName.substringAfterLast('.')
        return KiraScope().singleValue(paramName, value withName objectValueName, type).build()
    }
}

public class NullableObjectSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public var qualifiedName: String,
    public val value: T,
    public var isNullByDefault: Boolean,
) : SupplierBuilder<T?>() {
    override fun provideSupplier(): Supplier<T?> {
        val type = ClassType(qualifiedName, ClassType.Variant.OBJECT)
        val objectValueName = qualifiedName.substringAfterLast('.')
        return KiraScope().nullableSingleValue(
            paramName, value withName objectValueName, type, isNullByDefault
        ).build()
    }
}

internal object ObjectInDataClass : DataClassSupplierSupport {
    override fun KiraScope.provideSupplierBuilderForParam(
        param: KParameter, paramClass: KClass<Any>, nullable: Boolean, defaultValue: Any?
    ): SupplierBuilder<*>? {
        if (paramClass.objectInstance == null) return null
        val paramClassName = paramClass.qualifiedName!!
        val objectInstance = paramClass.objectInstance!!
        return if (nullable) nullableObject(
            param.name!!, paramClassName, objectInstance, defaultValue == null
        )
        else `object`(
            param.name!!, paramClassName, objectInstance,
        )
    }
}

private object Asd

@Preview
@Composable
private fun Preview() = KiraScope().`object`("param name", "Asd", Asd).build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableObject("param name", "Asd", Asd).build().Ui()
