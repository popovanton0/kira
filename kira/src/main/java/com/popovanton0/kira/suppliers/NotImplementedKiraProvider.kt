package com.popovanton0.kira.suppliers

import com.popovanton0.kira.suppliers.compound.KiraScope
import kotlin.reflect.KClass

/**
 * Generated [KiraProvider] — [T] doesn't have an empty constructor. Create an instance yourself and
 * add it to the [KiraRegistry].
 */
public inline fun <reified T : KiraProvider<*>> TODO(): KiraProvider<*> =
    NotImplementedKiraProvider(T::class)

public class NotImplementedKiraProvider @PublishedApi internal constructor(
    private val kiraProvider: KClass<out KiraProvider<*>>
) : KiraProvider<KiraScope> {
    override val kira: Kira<KiraScope>
        get() = throw NotImplementedError(
            """
                    |Generated KiraProvider "${kiraProvider.qualifiedName}" doesn't have an empty 
                    |constructor. Create an instance yourself and add it to the KiraRegistry"""
                .trimMargin()
        )
}