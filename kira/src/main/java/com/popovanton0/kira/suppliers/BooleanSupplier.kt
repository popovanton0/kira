package com.popovanton0.kira.suppliers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumTouchTargetEnforcement
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.suppliers.base.PropertyBasedSupplier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.dataclass.DataClassSupplierSupport
import com.popovanton0.kira.ui.ListItem
import com.popovanton0.kira.ui.RadioButton
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSuperclassOf

public fun KiraScope.boolean(
    paramName: String,
    defaultValue: Boolean = false,
): BooleanSupplierBuilder =
    BooleanSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public fun KiraScope.nullableBoolean(
    paramName: String,
    defaultValue: Boolean? = null,
): NullableBooleanSupplierBuilder =
    NullableBooleanSupplierBuilder(paramName, defaultValue).also(::addSupplierBuilder)

public class BooleanSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean,
) : SupplierBuilder<Boolean>() {
    override fun provideSupplier(): Supplier<Boolean> =
        NullableBooleanSupplierImpl(paramName, defaultValue, nullable = false)
}

public class NullableBooleanSupplierBuilder internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean?,
) : SupplierBuilder<Boolean?>() {
    override fun provideSupplier(): Supplier<Boolean?> =
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
                label = paramName,
            )
        } else {
            BooleanSwitch(
                checked = currentValue!!,
                onCheckedChange = { currentValue = it as T },
                label = paramName,
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun BooleanSwitch(
        modifier: Modifier = Modifier,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        label: String,
    ): Unit = ListItem(
        modifier = modifier.clickable { onCheckedChange(!checked) },
        overlineText = { Text(text = "Boolean") },
        text = { Text(text = label) },
        end = {
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Switch(
                    modifier = modifier,
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }
        }
    )

    @Composable
    fun NullableBooleanSwitch(
        modifier: Modifier = Modifier,
        checked: Boolean?,
        onCheckedChange: (Boolean?) -> Unit,
        label: String,
    ): Unit = ListItem(
        modifier = modifier,
        overlineText = { Text(text = "Boolean?") },
        text = { Text(text = label) },
        end = {
            Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                RadioButton("null", selected = checked == null) { onCheckedChange(null) }
                RadioButton("false", selected = checked == false) { onCheckedChange(false) }
                RadioButton("true", selected = checked == true) { onCheckedChange(true) }
            }
        }
    )
}

internal object BooleanInDataClass : DataClassSupplierSupport {
    override fun KiraScope.provideSupplierBuilderForParam(
        param: KParameter, paramClass: KClass<Any>, nullable: Boolean, defaultValue: Any?
    ): SupplierBuilder<*>? = when {
        !paramClass.isSuperclassOf(Boolean::class) -> null
        nullable -> nullableBoolean(param.name!!, defaultValue as Boolean?)
        defaultValue != null -> boolean(param.name!!, defaultValue as Boolean)
        else -> boolean(param.name!!)
    }
}

@Preview
@Composable
private fun Preview() = KiraScope().boolean("param name").build().Ui()

@Preview
@Composable
private fun NullablePreview() = KiraScope().nullableBoolean("param name").build().Ui()
