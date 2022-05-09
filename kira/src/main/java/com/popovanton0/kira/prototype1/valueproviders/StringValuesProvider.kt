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

public fun stringValuesProvider(paramName: String, defaultValue: String): StringValuesProvider =
    StringValuesProvider(paramName, defaultValue)

public fun KiraScope.stringValuesProvider(
    paramName: String,
    defaultValue: String,
): StringValuesProvider = StringValuesProvider(paramName, defaultValue).also(::addValuesProvider)

public class StringValuesProvider internal constructor(
    public var paramName: String,
    public var defaultValue: String,
) : ValuesProvider<String> {
    private lateinit var delegate: StringValuesProviderImpl

    @Composable
    override fun currentValue(): String = delegate.currentValue()

    @Composable
    override fun Ui(): Unit = delegate.Ui()

    override fun initialize() {
        delegate = StringValuesProviderImpl(paramName, defaultValue)
    }
}

private class StringValuesProviderImpl(
    private val paramName: String,
    defaultValue: String,
) : PropertyBasedValuesProvider<String> {
    override var currentValue: String by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = TextField(
        value = currentValue,
        onValueChange = { currentValue = it },
        label = paramName
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