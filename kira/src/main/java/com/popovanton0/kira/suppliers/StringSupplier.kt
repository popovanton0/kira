package com.popovanton0.kira.suppliers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.ListItem

public fun KiraScope.string(
    paramName: String,
    defaultValue: String = "Lorem",
): StringSupplierBuilder =
    StringSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public fun KiraScope.nullableString(
    paramName: String,
    defaultValue: String? = null,
): NullableStringSupplierBuilder =
    NullableStringSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public class StringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String,
) : SupplierBuilder<String>() {
    override fun provideSupplier(): Supplier<String> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = false)
}

public class NullableStringSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: String?,
) : SupplierBuilder<String?>() {
    override fun provideSupplier(): Supplier<String?> =
        NullableStringSupplierImpl(paramName, defaultValue, nullable = true)
}

private open class NullableStringSupplierImpl<T : String?>(
    private val paramName: String,
    defaultValue: T,
    private val nullable: Boolean,
) : PropertyBasedSupplier<T> {
    override var currentValue: T by mutableStateOf(defaultValue)

    @Composable
    override fun Ui(params: Any?) {
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

    @Composable
    fun TextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
    ): Unit = ListItem {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) }
        )
    }

    @Composable
    fun NullableTextField(
        modifier: Modifier = Modifier,
        value: String?,
        onValueChange: (String?) -> Unit,
        label: String,
    ) {
        var latestNonNullValue by remember { mutableStateOf("") }
        ListItem(
            text = {
                OutlinedTextField(
                    modifier = modifier.fillMaxWidth(),
                    value = value ?: latestNonNullValue,
                    onValueChange = {
                        latestNonNullValue = it
                        onValueChange(it)
                    },
                    enabled = value != null,
                    label = { Text(text = label) }
                )
            },
            end = {
                Checkbox(
                    label = "null",
                    checked = value == null,
                    onCheckedChange = { onValueChange(if (value == null) latestNonNullValue else null) }
                )
            }
        )
    }
}


@Preview
@Composable
private fun Preview() = KiraScope().string("param name").build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableString("param name").build().Ui()