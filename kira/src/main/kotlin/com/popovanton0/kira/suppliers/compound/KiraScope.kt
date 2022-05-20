package com.popovanton0.kira.suppliers.compound

import androidx.compose.runtime.Composable
import com.popovanton0.kira.suppliers.base.Supplier

public open class KiraScope {
    private lateinit var suppliers: MutableList<Supplier<*>>

    public fun addSupplier(supplier: Supplier<*>) {
        if (!::suppliers.isInitialized) suppliers = mutableListOf()
        suppliers.add(supplier)
    }

    public open fun collectSuppliers(): List<Supplier<*>> {
        if (!::suppliers.isInitialized) suppliers = mutableListOf()
        return suppliers
    }

    public fun <T> injector(block: @Composable () -> T): Injector<T> = Injector(block)
}


public class Injector<T> internal constructor(internal val injector: @Composable () -> T) {
    @Composable
    public operator fun invoke(): T = injector()
}