package com.popovanton0.kira.suppliers.compound

import com.popovanton0.kira.suppliers.base.Supplier

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScope : KiraScope() {
    public abstract override fun collectSuppliers(): List<Supplier<*>>
}

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScopeWithImpls<R : GeneratedKiraScopeWithImpls.SupplierImplsScope> : KiraScope() {
    public abstract class SupplierImplsScope : KiraScope()

    protected abstract val supplierImplsScope: R
    public fun generatedSupplierImpls(block: R.() -> Unit): Unit = supplierImplsScope.block()
    public abstract override fun collectSuppliers(): List<Supplier<*>>
}