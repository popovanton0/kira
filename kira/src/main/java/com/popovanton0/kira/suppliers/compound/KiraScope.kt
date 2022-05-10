package com.popovanton0.kira.suppliers.compound

import androidx.compose.runtime.Composable
import com.popovanton0.kira.suppliers.base.Supplier

public open class KiraScope {
    internal val suppliers = mutableListOf<Supplier<*>>()

    public fun addSupplier(supplier: Supplier<*>) {
        suppliers.add(supplier)
    }
}

public fun <T> KiraScope.injector(block: @Composable () -> T): Injector<T> = Injector(block)

public class Injector<T> internal constructor(internal val injector: @Composable () -> T)