package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.`object`
import kotlin.reflect.KClass

public fun <T : Any> KiraScope.sealedClass(
    paramName: String,
    sealedClass: KClass<T>,
    defaultValue: T,
): Supplier<T> =
    SealedClassSupplier(paramName, sealedClass, defaultValue)//.also(::addSupplierBuilder)


private class SealedClassSupplier<T : Any>(
    private val paramName: String,
    private val sealedClass: KClass<T>,
    private val defaultValue: T,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    init {
    }


    private fun <N : Any> map(sealedClass: KClass<N>) {
        require(sealedClass.isSealed)
        val sealedSubclasses = sealedClass.sealedSubclasses
        sealedSubclasses.map { subclass ->
            val objectInstance = subclass.objectInstance
            when {
                objectInstance != null -> {
                    KiraScope().`object`(paramName,"zdf", objectInstance)
                }
                subclass.isSealed -> {
                    map(subclass)
                }
                subclass.isData -> {

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
