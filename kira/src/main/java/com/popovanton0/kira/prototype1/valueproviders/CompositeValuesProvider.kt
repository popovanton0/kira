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

public fun <T : Any> ParameterDetails.composite(
    label: String,
    vararg providers: ValuesProvider<*>,
    provider: @Composable () -> T, // injector, builder, producer, creator, factory, provider
): ValuesProvider<T> = CompositeValuesProvider(
    parameterDetails = this,
    providers = providers,
    label = label,
    provider = provider,
    nullable = false,
    isNullByDefault = false
) as ValuesProvider<T>

public fun <T : Any> ParameterDetails.nullableComposite(
    label: String,
    vararg providers: ValuesProvider<*>,
    isNullByDefault: Boolean = false,
    provider: @Composable () -> T,
): ValuesProvider<T?> =
    CompositeValuesProvider(this, providers, label, provider, nullable = true, isNullByDefault)

@PublishedApi
internal class CompositeValuesProvider<T : Any>(
    private val parameterDetails: ParameterDetails,
    private val providers: Array<out ValuesProvider<*>>,
    private val label: String,
    private val provider: @Composable () -> T,
    private val nullable: Boolean,
    private val isNullByDefault: Boolean,
) : ValuesProvider<T?> {

    private lateinit var _currentValue: MutableState<T?>

    @Composable
    override fun currentValue(): T? {
        if (!::_currentValue.isInitialized) {
            val initialValue = if (isNullByDefault) null else provider()
            _currentValue = remember { mutableStateOf(initialValue) }
        }
        return _currentValue.value
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Ui() {
        if (!::_currentValue.isInitialized) currentValue()
        if (_currentValue.value != null) _currentValue.value = provider()
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
                val value = provider()
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
