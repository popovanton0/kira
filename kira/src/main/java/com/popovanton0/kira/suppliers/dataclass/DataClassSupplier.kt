package com.popovanton0.kira.suppliers.dataclass

import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.toClassType
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.compound
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.compound.nullableCompound
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

@ReflectionUsage
public fun <T : Any> KiraScope.dataClass(
    paramName: String,
    dataClass: KClass<T>,
    defaultValue: T? = null,
): DataClassSupplierBuilder<T> {
    require(dataClass.isData)
    return DataClassSupplierBuilder(paramName, dataClass, defaultValue).also(::addSupplierBuilder)
}

@ReflectionUsage
public fun <T : Any> KiraScope.nullableDataClass(
    paramName: String,
    dataClass: KClass<T>,
    defaultValue: T? = null,
): NullableDataClassSupplierBuilder<T> {
    require(dataClass.isData)
    return NullableDataClassSupplierBuilder(paramName, dataClass, defaultValue)
        .also(::addSupplierBuilder)
}

public class DataClassSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public val dataClass: KClass<T>,
    public val defaultValue: T?,
    private val nestingLevel: Int = 0,
) : SupplierBuilder<T>() {
    override fun provideSupplier(): Supplier<T> = KiraScope().compound(
        paramName, dataClass.toClassType()
    ) { configureCompoundSupplier(dataClass, defaultValue, nestingLevel) }.build()
}

public class NullableDataClassSupplierBuilder<T : Any> internal constructor(
    public var paramName: String,
    public val dataClass: KClass<T>,
    public val defaultValue: T?,
    private val nestingLevel: Int = 0,
) : SupplierBuilder<T?>() {
    override fun provideSupplier(): Supplier<T?> = KiraScope().nullableCompound(
        paramName, dataClass.toClassType(), defaultValue == null
    ) { configureCompoundSupplier(dataClass, defaultValue, nestingLevel) }.build()
}

private fun <T : Any> KiraScope.configureCompoundSupplier(
    dataClass: KClass<T>,
    defaultValue: T?,
    nestingLevel: Int = 0,
): Injector<T> {
    suppliers(dataClass, defaultValue, nestingLevel)
    val suppliers = collectSupplierBuilders().map { it.build() }
    return injector {
        val args = suppliers.map { it.currentValue() }.toTypedArray()
        dataClass.primaryConstructor!!.call(*args)
    }
}

@Suppress("UNCHECKED_CAST", "TYPE_MISMATCH_WARNING", "UPPER_BOUND_VIOLATED_WARNING")
private fun <T : Any> KiraScope.suppliers(
    dataClass: KClass<T>,
    defaultValue: T?,
    nestingLevel: Int,
) {
    val primaryConstructorParams = dataClass.primaryConstructor!!.parameters
    val defaultValues = defaultValue?.let { defaultValues(dataClass, primaryConstructorParams, it) }

    primaryConstructorParams.mapIndexed { paramIndex, param ->
        require(nestingLevel <= 100) {
            "Possibly infinite recursion detected: over 100 nested data classes"
        }
        @Suppress("NAME_SHADOWING")
        val defaultValue = defaultValues?.get(paramIndex)
        val nullable = param.type.isMarkedNullable
        val paramClass = param.type.classifier
        require(paramClass is KClass<*>) {
            "${param.name} param in data ${dataClass.qualifiedName} class is using generics, " +
                    "which are unsupported by DataClassSupplier OR has type that is not " +
                    "denotable in Kotlin, for example: an intersection type"
        }
        paramClass as KClass<Any>
        if (paramClass.isData) {
            val supplier = if (nullable) NullableDataClassSupplierBuilder(
                param.name!!, paramClass, defaultValue, nestingLevel + 1
            ) else DataClassSupplierBuilder(
                param.name!!, paramClass, defaultValue, nestingLevel + 1
            )
            addSupplierBuilder(supplier)
        } else {
            DataClassSupplierConfig.paramSupplierProviders.firstNotNullOfOrNull {
                with(it) {
                    provideSupplierBuilderForParam(param, paramClass, nullable, defaultValue)
                }
            } ?: error(
                "Unsupported param type ${paramClass.qualifiedName} in data class " +
                        "${dataClass.qualifiedName} found"
            )
        }
    }
}

private fun <T : Any> defaultValues(
    dataClass: KClass<T>,
    primaryConstructorParams: List<KParameter>,
    defaultValue: T
): List<Any?> {
    val primaryConstructorParamNames = primaryConstructorParams.map { it.name }
    return dataClass.declaredMemberProperties
        .filter { primaryConstructorParamNames.contains(it.name) }
        .sortedBy { primaryConstructorParamNames.indexOf(it.name) }
        .map { it.get(defaultValue) }
}
