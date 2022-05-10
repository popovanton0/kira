package com.popovanton0.kira.suppliers.compound

import com.popovanton0.kira.suppliers.base.Supplier

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScope<R : GeneratedKiraScope.SupplierImplsScope> : KiraScope() {
    public abstract class SupplierImplsScope : KiraScope()

    protected abstract val supplierImplsScope: R
    public abstract fun collectSuppliers(): List<Supplier<*>>
    public fun supplierImpls(block: R.() -> Unit): Unit = supplierImplsScope.block()
}