package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.ParameterDetails
import com.popovanton0.kira.prototype1.PropertyBasedValuesProvider
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.NullableTextField
import com.popovanton0.kira.ui.TextField

public fun ParameterDetails.string(defaultValue: String): ValuesProvider<String> =
    StringValuesProvider(defaultValue, this)

public fun ParameterDetails.nullableString(defaultValue: String?): ValuesProvider<String?> =
    NullableStringValuesProvider(defaultValue, this)

private class StringValuesProvider(
    defaultValue: String,
    private val parameterDetails: ParameterDetails,
) : PropertyBasedValuesProvider<String> {
    override var currentValue: String by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = TextField(
        value = currentValue,
        onValueChange = { currentValue = it },
        label = parameterDetails.name
    )
}

private class NullableStringValuesProvider(
    defaultValue: String?,
    private val parameterDetails: ParameterDetails,
) : PropertyBasedValuesProvider<String?> {
    override var currentValue: String? by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = NullableTextField(
        value = currentValue,
        onValueChange = { currentValue = it },
        label = parameterDetails.name
    )
}