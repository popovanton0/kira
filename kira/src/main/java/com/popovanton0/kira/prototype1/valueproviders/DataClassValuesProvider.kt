package com.popovanton0.kira.prototype1.valueproviders
/*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.ExperimentalKiraApi
import com.popovanton0.kira.prototype1.ParameterDetails
import com.popovanton0.kira.prototype1.PropertyBasedValuesProvider
import com.popovanton0.kira.prototype1.ValuesProvider
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

@ExperimentalKiraApi
public inline fun <reified T : Any> ParameterDetails.dataClass(defaultValue: T): ValuesProvider<T> {
    return dataClass(defaultValue, T::class)
}

@ExperimentalKiraApi
public fun <T : Any> ParameterDetails.dataClass(
    defaultValue: T,
    kClass: KClass<T>
): ValuesProvider<T> {
    require(kClass.isData) { "${kClass.qualifiedName} must be a data class" }
    val dataClassValuesProvider = DataClassValuesProvider(kClass, defaultValue)
    return composite(label = kClass.qualifiedName!!, dataClassValuesProvider) {
        dataClassValuesProvider.currentValue()
    }
}

//public inline fun <T : Any?> ParameterDetails.nullableDataClass(defaultValue: T): ValuesProvider<T?> =
//    NullableDataClassValuesProvider(defaultValue, this, nullable = true)

@PublishedApi
internal class DataClassValuesProvider<T : Any>(
    private val kClass: KClass<T>,
    defaultValue: T,
) : PropertyBasedValuesProvider<T> {

    private val primaryConstructor = kClass.primaryConstructor
        ?: error("Data class must have a primary constructor")
    private val properties: List<KProperty1<T, *>> = run {
        primaryConstructor.parameters.map { param ->
            kClass.declaredMemberProperties.find { prop -> prop.name == param.name }
                ?: error("no property found for param, impossible for data classes")
        }
    }
    @OptIn(ExperimentalKiraApi::class)
    private val valueProviders: List<ValuesProvider<*>> = properties.map { property ->
        val type = property.returnType
        val parameterDetails = ParameterDetails(property.name)
        val defaultVal = property.get(defaultValue)
        val classifier = type.classifier as KClass<Any>
        when {
            classifier == Boolean::class -> {
                if (type.isMarkedNullable)
                    nullableBoolean(parameterDetails, defaultVal as Boolean?)
                else
                    boolean(parameterDetails, defaultVal as Boolean)
            }
            classifier == String::class -> {
                if (type.isMarkedNullable)
                    nullableString(parameterDetails, defaultVal as String?)
                else
                    string(parameterDetails, defaultVal as String)
            }
            classifier.isSubclassOf(Enum::class) -> {
                classifier as KClass<Enum<*>>
                if (type.isMarkedNullable)
                    parameterDetails.nullableEnum(defaultVal as Enum<*>?, classifier)
                else
                    parameterDetails.enum(defaultVal as Enum<*>, classifier)
            }
            classifier.isData -> {
                if (classifier == kClass) error("Cyclic dependency, not supported")
                if (type.isMarkedNullable) TODO()
                else parameterDetails.dataClass(defaultVal as Any, classifier)
            }
            else -> error("Unknown")
        }
    }

    private val provider: @Composable () -> T = {
        primaryConstructor.call(*valueProviders.map { it.currentValue() }.toTypedArray())
    }

    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() {
        currentValue = provider()
        valueProviders.forEach { it.Ui() }
    }
}
*/
