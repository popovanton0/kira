package com.popovanton0.kira.suppliers

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Type
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.ListItem
import com.popovanton0.kira.ui.TypeUi

public fun <T> KiraScope.value(
    paramName: String,
    type: Type? = null,
    valueName: (T) -> String = { it.toString() },
    valueProvider: () -> T,
): SupplierBuilder<T> = object : SupplierBuilder<T>() {
    override fun provideSupplier(): Supplier<T> = object : Supplier<T> {
        @Composable
        override fun currentValue(): T = valueProvider()

        @Composable
        override fun Ui(params: Any?) = ListItem(
            overlineText = { if (type != null) TypeUi(type) },
            text = { Text(paramName) },
            end = { Text(text = valueName(currentValue())) }
        )
    }
}.also(::addSupplierBuilder)
