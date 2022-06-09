package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.CompoundSupplierBuilder
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.NullableCompoundSupplierBuilder
import com.popovanton0.kira.suppliers.compound.compound
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.suppliers.compound.nullableCompound

public fun <T : Any> KiraScope.singleValue(
    paramName: String,
    value: T,
    typeName: String = value::class.java.name,
): CompoundSupplierBuilder<T, KiraScope> = compound(paramName, typeName) { injector { value } }

public fun <T : Any> KiraScope.nullableSingleValue(
    paramName: String,
    value: T,
    typeName: String = value::class.java.name,
    nullByDefault: Boolean,
): NullableCompoundSupplierBuilder<T, KiraScope> = nullableCompound(
    paramName = paramName,
    typeName = typeName,
    isNullByDefault = nullByDefault
) {
    injector { value }
}

@Preview
@Composable
private fun Preview() =
    KiraScope().singleValue("param name", "string value").apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() =
    KiraScope().nullableSingleValue("param name", "string value", nullByDefault = true)
        .apply { initialize() }.Ui()