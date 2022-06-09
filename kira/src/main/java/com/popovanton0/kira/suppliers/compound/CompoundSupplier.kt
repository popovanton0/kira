package com.popovanton0.kira.suppliers.compound

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.ListItem
import com.popovanton0.kira.ui.VerticalDivider

public fun <T : Any> KiraScope.compound(
    paramName: String,
    typeName: String,
    block: KiraScope.() -> Injector<T>,
): CompoundSupplierBuilder<T, KiraScope> =
    compound(KiraScope(), paramName, typeName, block)

public fun <T : Any> KiraScope.nullableCompound(
    paramName: String,
    typeName: String,
    isNullByDefault: Boolean = false,
    block: KiraScope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, KiraScope> =
    nullableCompound(KiraScope(), paramName, typeName, isNullByDefault, block)

public fun <T : Any, Scope : KiraScope> KiraScope.compound(
    scope: Scope,
    paramName: String,
    typeName: String,
    block: Scope.() -> Injector<T>,
): CompoundSupplierBuilder<T, Scope> =
    CompoundSupplierBuilder(scope, paramName, typeName, block).also(::addSupplier)

public fun <T : Any, Scope : KiraScope> KiraScope.nullableCompound(
    scope: Scope,
    paramName: String,
    typeName: String,
    isNullByDefault: Boolean = false,
    block: Scope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, Scope> =
    NullableCompoundSupplierBuilder(scope, paramName, typeName, isNullByDefault, block)
        .also(::addSupplier)

public class CompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var typeName: String,
    block: Scope.() -> Injector<T>,
) : SupplierBuilder<T>() {
    private var injector: Injector<T> = scope.block()

    public fun modify(block: Scope.() -> Unit): CompoundSupplierBuilder<T, Scope> {
        if (!isInitialized) scope.block() else alreadyInitializedError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<T>) -> Injector<T>
    ): CompoundSupplierBuilder<T, Scope> {
        if (!isInitialized) injector = scope.block(injector) else alreadyInitializedError()
        return this
    }

    override fun BuildKey.build(): Supplier<T> = CompoundSupplierImpl(
        paramName = paramName,
        suppliers = scope.collectSuppliers().toList().onEach { it.initialize() },
        typeName = typeName,
        injector = injector.injector,
        nullable = false,
        isNullByDefault = false,
    ) as Supplier<T>
}

public class NullableCompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var typeName: String,
    public var isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>
) : SupplierBuilder<T?>() {
    private var injector: Injector<T> = scope.block()

    public fun modify(block: Scope.() -> Unit): NullableCompoundSupplierBuilder<T, Scope> {
        if (!isInitialized) scope.block() else alreadyInitializedError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<T>) -> Injector<T>
    ): NullableCompoundSupplierBuilder<T, Scope> {
        if (!isInitialized) injector = scope.block(injector) else alreadyInitializedError()
        return this
    }

    override fun BuildKey.build(): Supplier<T?> = CompoundSupplierImpl(
        paramName = paramName,
        suppliers = scope.collectSuppliers().toList().onEach { it.initialize() },
        typeName = typeName,
        injector = injector.injector,
        nullable = true,
        isNullByDefault = isNullByDefault,
    )
}

private class CompoundSupplierImpl<T : Any>(
    private val paramName: String,
    private val suppliers: List<Supplier<*>>,
    private val typeName: String,
    private val injector: @Composable () -> T,
    private val nullable: Boolean,
    private val isNullByDefault: Boolean,
) : Supplier<T?> {

    private lateinit var _currentValue: MutableState<T?>
    private lateinit var latestNonNullValue: T
    private var openDialog: Boolean by mutableStateOf(false)

    @SuppressLint("UnrememberedMutableState")
    @Composable
    override fun currentValue(): T? {
        if (!::_currentValue.isInitialized) {
            val initialValue = injector()
            latestNonNullValue = initialValue
            _currentValue = mutableStateOf(if (isNullByDefault) null else initialValue)
        }
        return _currentValue.value
    }

    @Composable
    override fun Ui(params: Any?) {
        Box(modifier = Modifier.hide()) {
            if (!::_currentValue.isInitialized) {
                currentValue()
            } else if (_currentValue.value != null) {
                val value = injector()
                latestNonNullValue = value
                _currentValue.value = value
            }
        }
        val params = params as CompoundParams?
        val compressIntoDialog = params != null && params.nestingLevel > 1

        if (compressIntoDialog) {
            Compressed()
            if (openDialog) CompoundDialog(params)
        } else {
            ListItem(
                sideSlotsAlignment = Alignment.Top,
                overlineText = { Text(text = typeName) },
                text = { Text(text = paramName) },
                secondaryText = secondaryText@{
                    if (suppliers.isEmpty()) return@secondaryText
                    VerticalDividerBox(modifier = Modifier.nullOverlayModifier()) {
                        // not lazy because kira root places us in the LazyColumn
                        // and LazyColumn in LazyColumn is prohibited
                        Column {
                            suppliers.forEach { supplier ->
                                if (!isCompoundSupplier(supplier)) supplier.Ui(params = null)
                                else CompoundSupplierUi(params, supplier)
                            }
                        }
                    }
                },
                end = {
                    if (nullable) NullCheckbox()
                }
            )
        }
    }

    @Composable
    private fun VerticalDividerBox(modifier: Modifier, content: @Composable () -> Unit) {
        Row(modifier = modifier.height(IntrinsicSize.Min)) {
            VerticalDivider(modifier = Modifier.padding(top = 8.dp))
            content()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun Modifier.hide() = alpha(0f).pointerInteropFilter { true }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun Modifier.nullOverlayModifier() = composed {
        val alpha by animateFloatAsState(
            targetValue = if (_currentValue.value == null) ContentAlpha.disabled else 1f
        )
        alpha(alpha).pointerInteropFilter { _currentValue.value == null }
    }

    @Composable
    private fun Compressed() = ListItem(
        modifier = Modifier.clickable { openDialog = true },
        overlineText = { Text(text = typeName) },
        text = { Text(text = paramName) },
        end = {
            Icon(
                painter = rememberVectorPainter(Icons.Default.ArrowForward),
                contentDescription = "open"
            )
        }
    )

    @Composable
    private fun CompoundDialog(params: CompoundParams?) {
        Dialog(onDismissRequest = { openDialog = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column {
                    ListItem(
                        overlineText = { Text(text = typeName) },
                        text = { Text(text = paramName) },
                        end = { if (nullable) NullCheckbox() }
                    )
                    Divider(Modifier.padding(horizontal = 16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nullOverlayModifier()
                    ) {
                        items(suppliers) { supplier ->
                            if (!isCompoundSupplier(supplier)) supplier.Ui(params = null)
                            else CompoundSupplierUi(params, supplier)
                        }
                    }
                    CloseDialogButton()
                }
            }
        }
    }

    @Composable
    private fun NullCheckbox() = Checkbox(
        label = "null",
        checked = _currentValue.value == null,
        onCheckedChange = { isNull ->
            _currentValue.value = if (isNull) null else latestNonNullValue
        }
    )

    @Composable
    private fun isCompoundSupplier(supplier: Supplier<*>) =
        supplier is NullableCompoundSupplierBuilder<*, *> ||
                supplier is CompoundSupplierBuilder<*, *>

    @Composable
    private fun CloseDialogButton() = Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            modifier = Modifier
                .padding(bottom = 4.dp, end = 8.dp)
                .align(Alignment.CenterEnd),
            onClick = { openDialog = false }
        ) {
            Text(text = stringResource(id = android.R.string.ok))
        }
    }

    @Composable
    private fun CompoundSupplierUi(
        params: CompoundParams?,
        supplier: Supplier<*>
    ) {
        supplier.Ui(params = CompoundParams(nestingLevel = (params?.nestingLevel ?: 0) + 1))
    }

    private data class CompoundParams(val nestingLevel: Int)
}

@Preview(showBackground = true)
@Composable
private fun Preview() =
    KiraScope().compound("param 1", "String") {
        boolean("bool param 1", false)
        nullableCompound("param 2", "String") {
            boolean("bool param 1", false)
            nullableCompound("param 3", "String") {
                boolean("bool param 1", false)
                injector { }
            }
            boolean("bool param 2", true)
            injector { }
        }
        boolean("bool param 2", true)
        injector { }
    }.apply { initialize() }.Ui()

@Preview
@Composable
private fun NullablePreview() =
    KiraScope().nullableCompound("param name", "String") {
        boolean("bool param 1", false)
        boolean("bool param 2", true)
        injector { }
    }.apply { initialize() }.Ui()
