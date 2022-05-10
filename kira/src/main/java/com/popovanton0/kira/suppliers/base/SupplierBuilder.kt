package com.popovanton0.kira.suppliers.base

import androidx.compose.runtime.Composable

public abstract class SupplierBuilder<T> : Supplier<T> {
    public class BuildKey internal constructor()

    public abstract fun build(key: BuildKey): Supplier<T>

    private lateinit var supplier: Supplier<T>
    public val isInitialized: Boolean get() = ::supplier.isInitialized

    @Composable
    override fun currentValue(): T = if (isInitialized) supplier.currentValue() else notInitError()

    @Composable
    override fun Ui(): Unit = if (isInitialized) supplier.Ui() else notInitError()

    public override fun initialize() {
        if (isInitialized) alreadyInitializedError()
        supplier = build(buildKey)
    }

    public fun alreadyInitializedError(): Nothing = error("Supplier is already initialized")

    public companion object {
        private val buildKey = BuildKey()
        private fun notInitError(): Nothing = error("Supplier is not initialized yet")
    }
}
