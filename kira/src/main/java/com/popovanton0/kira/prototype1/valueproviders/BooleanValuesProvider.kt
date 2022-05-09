package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.popovanton0.kira.prototype1.PropertyBasedValuesProvider
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.BooleanSwitch
import com.popovanton0.kira.ui.NullableBooleanSwitch

public fun KiraScope.boolean(
    paramName: String,
    defaultValue: Boolean,
): BooleanValuesProvider = BooleanValuesProvider(paramName, defaultValue).also(::addValuesProvider)

public fun KiraScope.nullableBoolean(
    paramName: String,
    defaultValue: Boolean?,
): NullableBooleanValuesProvider =
    NullableBooleanValuesProvider(paramName, defaultValue).also(::addValuesProvider)

public class BooleanValuesProvider internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean,
) : ValuesProvider<Boolean> {
    private lateinit var delegate: NullableBooleanValuesProviderImpl<Boolean>

    @Composable
    override fun currentValue(): Boolean = delegate.currentValue()!!

    @Composable
    override fun Ui(): Unit = delegate.Ui()

    override fun initialize() {
        delegate = NullableBooleanValuesProviderImpl(paramName, defaultValue, nullable = false)
    }
}

public class NullableBooleanValuesProvider internal constructor(
    public var paramName: String,
    public var defaultValue: Boolean?,
) : ValuesProvider<Boolean?> {
    private lateinit var delegate: NullableBooleanValuesProviderImpl<Boolean?>

    @Composable
    override fun currentValue(): Boolean? = delegate.currentValue()

    @Composable
    override fun Ui(): Unit = delegate.Ui()

    override fun initialize() {
        delegate = NullableBooleanValuesProviderImpl(paramName, defaultValue, nullable = true)
    }
}

/*private class BooleanValuesProviderImpl(
    paramName: String,
    defaultValue: Boolean,
) : NullableBooleanValuesProviderImpl<Boolean>(paramName, defaultValue, nullable = false)*/

private open class NullableBooleanValuesProviderImpl<T : Boolean?>(
    private val paramName: String,
    defaultValue: T?,
    private val nullable: Boolean,
) : PropertyBasedValuesProvider<T?> {
    override var currentValue: T? by mutableStateOf(defaultValue)

    @Composable
    override fun Ui() {
        if (nullable) {
            NullableBooleanSwitch(
                checked = currentValue,
                onCheckedChange = { currentValue = it as T? },
                label = paramName
            )
        } else {
            BooleanSwitch(
                checked = currentValue!!,
                onCheckedChange = { currentValue = it as T? },
                label = paramName
            )
        }
    }
}