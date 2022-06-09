package com.popovanton0.kira.suppliers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.BooleanSwitch
import com.popovanton0.kira.ui.NullableBooleanSwitch

public fun KiraScope.boolean(
    paramName: String,
    defaultValue: Boolean,
): BooleanSupplierBuilder = BooleanSupplierBuilder(paramName, defaultValue).also(::addSupplier)

public fun KiraScope.nullableBoolean(
    paramName: String,
    defaultValue: Boolean?,
): NullableBooleanSupplierBuilder =
    NullableBooleanSupplierBuilder(paramName, defaultValue).also(::addSupplier)

public class BooleanSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean,
) : SupplierBuilder<Boolean>() {
    override fun BuildKey.build(): Supplier<Boolean> =
        NullableBooleanSupplierImpl(paramName, defaultValue, nullable = false)
}

public class NullableBooleanSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean?,
) : SupplierBuilder<Boolean?>() {
    override fun BuildKey.build(): Supplier<Boolean?> =
        NullableBooleanSupplierImpl(paramName, defaultValue, nullable = true)
}

private open class NullableBooleanSupplierImpl<T : Boolean?>(
    private val paramName: String,
    defaultValue: T,
    private val nullable: Boolean,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui(params: Any?) {
        if (nullable) {
            NullableBooleanSwitch(
                checked = currentValue,
                onCheckedChange = { currentValue = it as T },
                label = paramName
            )
        } else {
            BooleanSwitch(
                checked = currentValue!!,
                onCheckedChange = { currentValue = it as T },
                label = paramName
            )
        }
    }
}

@Preview
@Composable
private fun Preview() =
    KiraScope().boolean("param name", defaultValue = false).apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() =
    KiraScope().nullableBoolean("param name", defaultValue = null).apply { initialize() }.Ui()
