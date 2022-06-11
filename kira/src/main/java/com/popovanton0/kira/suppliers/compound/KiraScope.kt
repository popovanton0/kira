package com.popovanton0.kira.suppliers.compound

import androidx.compose.runtime.Composable
import com.popovanton0.kira.suppliers.base.SupplierBuilder

public open class KiraScope {
    private lateinit var suppliers: MutableList<SupplierBuilder<*>>

    public fun addSupplierBuilder(supplier: SupplierBuilder<*>) {
        if (!::suppliers.isInitialized) suppliers = mutableListOf()
        suppliers.add(supplier)
    }

    public open fun collectSupplierBuilders(): List<SupplierBuilder<*>> {
        if (!::suppliers.isInitialized) suppliers = mutableListOf()
        return suppliers
    }
}

public fun <T> KiraScope.injector(block: @Composable () -> T): Injector<T> = Injector(block)


public class Injector<T> internal constructor(internal val injector: @Composable () -> T) {
    @Composable
    public operator fun invoke(): T = injector()
}