package com.popovanton0.kira.suppliers.dataclass

import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.string
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * This API provides an ability to add the support for extra types into the [dataClass] supplier.
 */
public interface DataClassSupplierSupport {
    /**
     * Implementation of this method must:
     * 1. validate, whether the particular supplier can supply [param];
     * 2. if [param] is NOT supported, return null. If supported:
     * 3. add the [SupplierBuilder] to [KiraScope] (usually just by calling the supplier function,
     * like [string])
     * 4. return the SupplierBuilder
     */
    public fun KiraScope.provideSupplierBuilderForParam(
        param: KParameter,
        paramClass: KClass<Any>,
        nullable: Boolean,
        defaultValue: Any?
    ): SupplierBuilder<*>?
}
