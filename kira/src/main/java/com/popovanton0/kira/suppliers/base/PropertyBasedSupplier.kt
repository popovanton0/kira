package com.popovanton0.kira.suppliers.base

import androidx.compose.runtime.Composable

public interface PropertyBasedSupplier<T> : Supplier<T> {
    public var currentValue: T

    @Composable
    override fun currentValue(): T = currentValue
}