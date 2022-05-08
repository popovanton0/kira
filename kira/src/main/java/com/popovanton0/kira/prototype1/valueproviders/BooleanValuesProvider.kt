package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.ParameterDetails
import com.popovanton0.kira.prototype1.PropertyBasedValuesProvider
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.BooleanSwitch
import com.popovanton0.kira.ui.NullableBooleanSwitch

public fun ParameterDetails.boolean(defaultValue: Boolean): ValuesProvider<Boolean> =
    BooleanValuesProvider(defaultValue, this)

public fun ParameterDetails.nullableBoolean(defaultValue: Boolean): ValuesProvider<Boolean?> =
    NullableBooleanValuesProvider(defaultValue, this)

private class BooleanValuesProvider(
    defaultValue: Boolean,
    private val parameterDetails: ParameterDetails,
) : PropertyBasedValuesProvider<Boolean> {
    override var currentValue: Boolean by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = BooleanSwitch(
        checked = currentValue,
        onCheckedChange = { currentValue = it },
        label = parameterDetails.name
    )
}

private class NullableBooleanValuesProvider(
    defaultValue: Boolean?,
    private val parameterDetails: ParameterDetails,
) : PropertyBasedValuesProvider<Boolean?> {
    override var currentValue: Boolean? by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = NullableBooleanSwitch(
        checked = currentValue,
        onCheckedChange = { currentValue = it },
        label = parameterDetails.name
    )
}