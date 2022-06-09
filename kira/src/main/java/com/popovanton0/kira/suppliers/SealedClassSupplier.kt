package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import kotlin.reflect.KClass

public inline fun <reified T : Any> sealedClass(defaultValue: T): Supplier<T> {
    return SealedClassSupplier(error(""), defaultValue, T::class)
}

/*public inline fun <reified T : SealedClass<*>?> ParameterDetails.nullableSealedClass(
    defaultValue: T?
): Supplier<T?> = NullableSealedClassSupplier(
    defaultValue = defaultValue,
    parameterDetails = this,
    sealedClassConstants = T::class.java.sealedClassConstants!!
        .toMutableList()
        .apply { add(0, null) }
        .toTypedArray()
)*/

@PublishedApi
internal class SealedClassSupplier<T : Any>(
    private val paramName: String,
    defaultValue: T,
    private val sealedClass: KClass<T>,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    init {
    }


    private fun <N : Any> map(sealedClass: KClass<N>): Unit {
        sealedClass.sealedSubclasses.map {
            val objectInstance = it.objectInstance
            when {
                objectInstance != null -> {
                    objectInstance
                }
                it.isSealed -> {

                }
                it.isData -> {

                }
                else -> {
                    // cannot autogenerate
                }
            }
        }
    }

    @Composable
    override fun Ui(params: Any?) {

    }
}
