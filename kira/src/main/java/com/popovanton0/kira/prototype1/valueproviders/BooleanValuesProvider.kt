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

public fun booleanValuesProvider(paramName: String, defaultValue: Boolean): BooleanValuesProvider =
    BooleanValuesProvider(paramName, defaultValue)

public fun CompositeValuesProviderScope.booleanValuesProvider(
    paramName: String,
    defaultValue: Boolean,
): BooleanValuesProvider =
     BooleanValuesProvider(paramName, defaultValue).also(::addValuesProvider)

public class BooleanValuesProvider internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean,
) : ValuesProvider<Boolean> {
    private lateinit var delegate: BooleanValuesProviderImpl

    @Composable
    override fun currentValue(): Boolean = delegate.currentValue()

    @Composable
    override fun Ui(): Unit = delegate.Ui()

    override fun initialize() {
        delegate = BooleanValuesProviderImpl(paramName, defaultValue)
    }
}

private class BooleanValuesProviderImpl(
    private val paramName: String,
    defaultValue: Boolean,
) : PropertyBasedValuesProvider<Boolean> {
    override var currentValue: Boolean by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() = BooleanSwitch(
        checked = currentValue,
        onCheckedChange = { currentValue = it },
        label = paramName
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