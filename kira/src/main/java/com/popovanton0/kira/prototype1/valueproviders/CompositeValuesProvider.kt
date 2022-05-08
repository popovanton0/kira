package com.popovanton0.kira.prototype1.valueproviders

import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.prototype1.ParameterDetails
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.VerticalDivider


public open class CompositeValuesProviderScope {
    internal val valuesProviders = mutableListOf<ValuesProvider<*>>()
    //public open fun addAllValuesProviders(): Unit = Unit

    public fun <T> injector(block: @Composable () -> T): Injector<T> = Injector(block)

    public fun addValuesProvider(valuesProvider: ValuesProvider<*>) {
        valuesProviders.add(valuesProvider)
    }
}

public data class Injector<T> internal constructor(val injector: @Composable () -> T)

public fun <T : Any, Scope : CompositeValuesProviderScope>
        compositeValuesProvider(
    scope: Scope,
    paramName: String,
    label: String,
    block: Scope.() -> Injector<T>,
): CompositeValuesProvider<T, Scope> =
    CompositeValuesProvider(scope, paramName, label, block)

public fun <T : Any, Scope : CompositeValuesProviderScope>
        nullableCompositeValuesProvider(
    scope: Scope,
    paramName: String,
    label: String,
    isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>,
): NullableCompositeValuesProvider<T, Scope> =
    NullableCompositeValuesProvider(scope, paramName, label, isNullByDefault, block)

public fun <T : Any, Scope : CompositeValuesProviderScope>
        CompositeValuesProviderScope.compositeValuesProvider(
    scope: Scope,
    paramName: String,
    label: String,
    block: Scope.() -> Injector<T>,
): CompositeValuesProvider<T, Scope> =
    CompositeValuesProvider(scope, paramName, label, block).also(::addValuesProvider)

public fun <T : Any, Scope : CompositeValuesProviderScope>
        CompositeValuesProviderScope.nullableCompositeValuesProvider(
    scope: Scope,
    paramName: String,
    label: String,
    isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>,
): NullableCompositeValuesProvider<T, Scope> =
    NullableCompositeValuesProvider(scope, paramName, label, isNullByDefault, block)
        .also(::addValuesProvider)

public class CompositeValuesProvider<T : Any, Scope : CompositeValuesProviderScope>
internal constructor(
    public val scope: Scope,
    public var paramName: String,
    public var label: String,
    block: Scope.() -> Injector<T>
) : ValuesProvider<T> {
    private lateinit var delegate: ValuesProvider<T>
    private val injector: Injector<T> = scope.block()

    override fun initialize() {
        //scope.addAllValuesProviders()
        val valueProviders = scope.valuesProviders.toList()
        valueProviders.forEach { it.initialize() } // todo where to initialize
        delegate = CompositeValuesProviderImpl(
            parameterDetails = ParameterDetails(paramName),
            providers = valueProviders,
            label = label,
            injector = injector.injector,
            nullable = false,
            isNullByDefault = false,
        ) as ValuesProvider<T>
    }

    @Composable
    override fun currentValue(): T = delegate.currentValue()

    @Composable
    override fun Ui(): Unit = delegate.Ui()
}

public class NullableCompositeValuesProvider<T : Any, Scope : CompositeValuesProviderScope>
internal constructor(
    public val scope: Scope,
    public var paramName: String,
    public var label: String,
    public var isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>
) : ValuesProvider<T?> {
    private lateinit var delegate: ValuesProvider<T?>
    private val injector: Injector<T> = scope.block()

    override fun initialize() {
        //scope.addAllValuesProviders()
        val valueProviders = scope.valuesProviders.toList()
        valueProviders.forEach { it.initialize() } // todo where to initialize
        delegate = CompositeValuesProviderImpl(
            parameterDetails = ParameterDetails(paramName),
            providers = valueProviders,
            label = label,
            injector = injector.injector,
            nullable = true,
            isNullByDefault = isNullByDefault,
        )
    }

    @Composable
    override fun currentValue(): T? = delegate.currentValue()

    @Composable
    override fun Ui(): Unit = delegate.Ui()
}

private class CompositeValuesProviderImpl<T : Any>(
    private val parameterDetails: ParameterDetails,
    private val providers: List<ValuesProvider<*>>,
    private val label: String,
    private val injector: @Composable () -> T,
    private val nullable: Boolean,
    private val isNullByDefault: Boolean,
) : ValuesProvider<T?> {

    private lateinit var _currentValue: MutableState<T?>

    @Composable
    override fun currentValue(): T? {
        if (!::_currentValue.isInitialized) {
            val initialValue = if (isNullByDefault) null else injector()
            _currentValue = remember { mutableStateOf(initialValue) }
        }
        return _currentValue.value
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Ui() {
        if (!::_currentValue.isInitialized) currentValue()
        if (_currentValue.value != null) _currentValue.value = injector()
        Row {
            ListItem(
                modifier = Modifier.weight(1f),
                overlineText = { Text(text = "Type: $label") }, // TODO localize
                text = { Text(text = parameterDetails.name) },
                secondaryText = {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .run {
                                if (_currentValue.value == null) {
                                    alpha(ContentAlpha.disabled).pointerInteropFilter { false }
                                } else this
                            },
                    ) {
                        VerticalDivider()
                        Column {
                            providers.forEach { it.Ui() }
                        }
                    }
                },
            )
            if (nullable) Box(modifier = Modifier.padding(end = 16.dp)) {
                val value = injector()
                Checkbox(
                    label = "null",
                    checked = _currentValue.value == null,
                    onCheckedChange = { isNull ->
                        _currentValue.value = if (isNull) null else value
                    }
                )
            }
        }
    }
}
