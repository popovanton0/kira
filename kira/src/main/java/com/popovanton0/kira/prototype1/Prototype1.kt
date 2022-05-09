package com.popovanton0.kira.prototype1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

public interface FunctionParameters<ReturnType> {
    public val valueProviders: List<ValuesProvider<*>>

    @Composable
    public fun invoke(): ReturnType

    /*public companion object {
        public operator fun <T> invoke(valueProviders: List<ValuesProvider<*>>): FunctionParameters<T> = object : FunctionParameters<T> {
            override val valueProviders: List<ValuesProvider<*>> = valueProviders

            @Composable
            override fun invoke(): T {
                valueProviders.forEach { it. }
            }

        }
    }*/
}

public data class ParameterDetails(
    val name: String,
)

public typealias ValuesProviderProvider<T> = ParameterDetails.() -> ValuesProvider<T>

public enum class Skill { LOW, OK, SICK }
public enum class Food { BAD, GOOD, EXCELLENT }
public data class Engine(
    val model: String = "Merlin",
    val diesel: Boolean = false,
)

public data class Car(
    val model: String = "Tesla",
    val lame: Boolean = false,
    val lameN: Boolean? = null,
    val cookerQuality: Food? = null,
    val engine: Engine = Engine(),
)

public interface PropertyBasedValuesProvider<T> : ValuesProvider<T> {
    public var currentValue: T

    @Composable
    override fun currentValue(): T = currentValue
}

public interface ValuesProvider<T> {

    @Composable
    public fun currentValue(): T

    @Composable
    public fun Ui()

    public fun initialize(): Unit = Unit

    /*
    TODO Char

    TODO Byte
    TODO Short
    TODO Int
    TODO Long

    TODO Float
    TODO Double

    TODO String
    */
}

public class KiraViewModel<ReturnType : Any>(
    internal val params: FunctionParameters<ReturnType>
) : ViewModel() {
    init {
        params.valueProviders.forEach { it.initialize() }
    }
}

@Composable
public fun DefaultHeader(function: @Composable () -> Unit): Unit = Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
            shape = RoundedCornerShape(12.dp)
        )
        .padding(40.dp),
    contentAlignment = Alignment.Center,
) {
    function()
}

@Composable
public fun <ReturnType : Any> KiraScreen(
    params: FunctionParameters<ReturnType>,
    header: @Composable (function: @Composable () -> Unit) -> Unit = { DefaultHeader(it) }
) {
    val vm = viewModel<KiraViewModel<ReturnType>>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            KiraViewModel(params) as T
    })
    val params = vm.params
    Box {
        LazyColumn {
            item { header { params.invoke() } }
            items(params.valueProviders) { it.Ui() }
        }
        EarlyPreview()
    }
}

@Composable
internal fun BoxScope.EarlyPreview() = Watermark(text = "Early Preview")

@Composable
internal fun BoxScope.Watermark(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = Color.Red.copy(alpha = 0.8f),
    textColor: Color = Color.White,
    alignment: Alignment = Alignment.TopEnd
) {
    Box(
        modifier = modifier
            .align(alignment)
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Text(text = text, color = textColor)
    }
}
