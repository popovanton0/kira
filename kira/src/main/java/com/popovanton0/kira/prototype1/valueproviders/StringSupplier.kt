package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.PropertyBasedSupplier
import com.popovanton0.kira.prototype1.Supplier
import com.popovanton0.kira.prototype1.SupplierBuilder
import com.popovanton0.kira.ui.NullableTextField
import com.popovanton0.kira.ui.TextField

public fun KiraScope.string(
    paramName: String,
    defaultValue: String,
): StringSupplierBuilder =
    StringSupplierBuilder(paramName, defaultValue).also(::addSupplier)

public fun KiraScope.nullableString(
    paramName: String,
    defaultValue: String?,
): NullableStringSupplierBuilder =
    NullableStringSupplierBuilder(paramName, defaultValue).also(::addSupplier)

public class StringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String,
) : SupplierBuilder<String>() {
    override fun build(key: BuildKey): Supplier<String> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = false)
}

public class NullableStringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String?,
) : SupplierBuilder<String?>() {
    override fun build(key: BuildKey): Supplier<String?> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = true)
}

private open class NullableStringSupplierImpl<T : String?>(
    private val paramName: String,
    defaultValue: T,
    private val nullable: Boolean,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() {
        if (nullable) {
            NullableTextField(
                value = currentValue,
                onValueChange = { currentValue = it as T },
                label = paramName
            )
        } else {
            TextField(
                value = currentValue!!,
                onValueChange = { currentValue = it as T },
                label = paramName
            )
        }
    }
}