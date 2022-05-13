package com.popovanton0.kira.suppliers

import com.popovanton0.kira.suppliers.compound.*

public fun <T : Any> KiraScope.singleValue(
    paramName: String,
    value: T,
    typeName: String = value::class.java.name,
): CompoundSupplierBuilder<T, KiraScope> = compound(paramName, typeName) {
    injector { value }
}

public fun <T : Any> KiraScope.nullableSingleValue(
    paramName: String,
    value: T,
    typeName: String = value::class.java.name,
    nullByDefault: Boolean,
): NullableCompoundSupplierBuilder<T, KiraScope> = nullableCompound(
    paramName = paramName,
    label = typeName,
    isNullByDefault = nullByDefault
) {
    injector { value }
}
