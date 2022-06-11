package com.popovanton0.kira.suppliers.base

import androidx.compose.runtime.Stable

public abstract class SupplierBuilder<T> {

    private lateinit var supplier: Supplier<T>
    public val isBuilt: Boolean get() = ::supplier.isInitialized

    protected abstract fun provideSupplier(): Supplier<T>

    @Stable
    public fun build(): Supplier<T> {
        if (::supplier.isInitialized) return supplier
        supplier = provideSupplier()
        return supplier
    }
}
