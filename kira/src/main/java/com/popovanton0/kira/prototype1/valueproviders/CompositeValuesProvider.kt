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
import com.popovanton0.kira.prototype1.ValueProviderBuilder
import com.popovanton0.kira.prototype1.ValuesProvider
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.VerticalDivider

public fun <T : Any, P1> composite2(
    paramName: String,
    label: String,
    provider1: ValuesProvider<P1>,
    injector: @Composable (P1) -> T, // injector, builder, producer, creator, factory, provider
): ValuesProvider<T> = TODO()

public fun <T : Any, P1, P2> composite2(
    paramName: String,
    label: String,
    provider1: ValuesProvider<P1>,
    provider2: ValuesProvider<P2>,
    injector: @Composable (P1, P2) -> T, // injector, builder, producer, creator, factory, provider
): CompositeValueProviderBuilder<T> {
    val valueProviders = listOf(provider1, provider2)
    injector(valueProviders[0] as P1, valueProviders[1] as P2)
    TODO()
}

public class CompositeValueProviderBuilder3Scope internal constructor() {
    internal val valueProviderBuilders = mutableListOf<ValueProviderBuilder<*>>()

    public fun addValueProviderBuilder(valuesProvider: ValueProviderBuilder<*>) {
        valueProviderBuilders.add(valuesProvider)
    }
}

public data class Injector<T>(val injector: @Composable () -> T)

public data class CompositeValueProviderBuilder3<T : Any>(
    public val paramName: String,
    public val label: String,
    public val block: CompositeValueProviderBuilder3Scope.() -> Injector<T>
): ValueProviderBuilder<T> {
    override fun build(): ValuesProvider<T> {
        val scope = CompositeValueProviderBuilder3Scope()
        val injector = scope.block().injector
        return CompositeValuesProvider(
            parameterDetails = ParameterDetails(paramName),
            /**
             * create a copy using [map] so that editing of the original list would not interfere
             */
            providers = scope.valueProviderBuilders.map { it.build() },
            label = label,
            injector = injector,
            nullable = false,
            isNullByDefault = false,
        ) as ValuesProvider<T>
    }
}

public interface CompositeValueProviderBuilder2<T : Any> {
    public val paramName: String
    public val label: String
    public val provider1: ValuesProvider<P1>
    public val provider2: ValuesProvider<P2>
    public val injector: @Composable (P1, P2) -> T
}

public data class CompositeValueProviderBuilder<T : Any, P1, P2> internal constructor(
    val paramName: String,
    val label: String,
    val provider1: ValueProviderBuilder<P1>,
    val provider2: ValueProviderBuilder<P2>,
    val injector: @Composable (P1, P2) -> T,
) : ValueProviderBuilder<T> {
    override fun build(): ValuesProvider<T> = CompositeValuesProvider(
        parameterDetails = ParameterDetails(paramName),
        providers = listOf(provider1.build(), provider2.build()),
        label = label,
        injector = { injector(provider1.currentValue(), provider2.currentValue()) },
        nullable = false,
        isNullByDefault = false,
    ) as ValuesProvider<T>
}

public fun <T : Any> ParameterDetails.composite(
    label: String,
    vararg providers: ValuesProvider<*>,
    injector: @Composable () -> T, // injector, builder, producer, creator, factory, provider
): ValuesProvider<T> = CompositeValuesProvider(
    parameterDetails = this,
    providers = providers,
    label = label,
    injector = injector,
    nullable = false,
    isNullByDefault = false
) as ValuesProvider<T>

public fun <T : Any> ParameterDetails.nullableComposite(
    label: String,
    vararg providers: ValuesProvider<*>,
    isNullByDefault: Boolean = false,
    injector: @Composable () -> T,
): ValuesProvider<T?> =
    CompositeValuesProvider(this, providers, label, injector, nullable = true, isNullByDefault)

@PublishedApi
internal class CompositeValuesProvider<T : Any>(
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
