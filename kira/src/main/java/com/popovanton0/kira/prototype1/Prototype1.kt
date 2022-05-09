package com.popovanton0.kira.prototype1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.popovanton0.kira.prototype1.valueproviders.RootCompoundSupplierBuilder

public interface PropertyBasedSupplier<T> : Supplier<T> {
    public var currentValue: T

    @Composable
    override fun currentValue(): T = currentValue
}

public interface Supplier<T> {

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

public abstract class SupplierBuilder<T> : Supplier<T> {
    public class BuildKey internal constructor()

    public abstract fun build(key: BuildKey): Supplier<T>

    private lateinit var supplier: Supplier<T>

    @Composable
    override fun currentValue(): T =
        if (::supplier.isInitialized) supplier.currentValue() else notInitError()

    @Composable
    override fun Ui(): Unit =
        if (::supplier.isInitialized) supplier.Ui() else notInitError()

    override fun initialize() {
        supplier = build(buildKey)
    }

    private companion object {
        private val buildKey = BuildKey()
        private fun notInitError(): Nothing = error("Supplier is not initialized yet")
    }
}

private class KiraViewModel<ReturnType : Any>(
    val root: RootCompoundSupplierBuilder<ReturnType, *>
) : ViewModel() {
    init {
        root.initialize()
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
public fun <ReturnType : Any> KiraScreen(root: RootCompoundSupplierBuilder<ReturnType, *>) {
    val vm = viewModel<KiraViewModel<ReturnType>>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            KiraViewModel(root) as T
    })
    Box {
        Column(Modifier.verticalScroll(rememberScrollState())) { vm.root.Ui() }
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
