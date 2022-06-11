package com.popovanton0.kira.suppliers.compound

import com.popovanton0.kira.suppliers.base.SupplierBuilder

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScope : KiraScope() {
    public abstract override fun collectSupplierBuilders(): List<SupplierBuilder<*>>
}

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScopeWithImpls
<R : GeneratedKiraScopeWithImpls.SupplierImplsScope> : GeneratedKiraScope() {

    protected abstract val `$$$supplierImplsScope$$$`: R

    public fun generated(block: R.() -> Unit): Unit = `$$$supplierImplsScope$$$`.block()

    public abstract override fun collectSupplierBuilders(): List<SupplierBuilder<*>>

    public abstract class SupplierImplsScope : KiraScope() {
        public fun implChanged(): Nothing = error(
            "Generated supplier implementation has been substituted by a Supplier of a " +
                    "different type"
        )
    }
}