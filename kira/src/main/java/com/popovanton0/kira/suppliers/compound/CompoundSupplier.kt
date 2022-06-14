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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.popovanton0.kira.suppliers.base.ClassType
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Type
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.boolean
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.ListItem
import com.popovanton0.kira.ui.TypeUi
import com.popovanton0.kira.ui.VerticalDivider

public fun <T : Any> KiraScope.compound(
    paramName: String,
    type: Type,
    block: KiraScope.() -> Injector<T>,
): CompoundSupplierBuilder<T, KiraScope> =
    compound(KiraScope(), paramName, type, block)

public fun <T : Any> KiraScope.nullableCompound(
    paramName: String,
    type: Type,
    isNullByDefault: Boolean = false,
    block: KiraScope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, KiraScope> =
    nullableCompound(KiraScope(), paramName, type, isNullByDefault, block)

public fun <T : Any, Scope : KiraScope> KiraScope.compound(
    scope: Scope,
    paramName: String,
    type: Type,
    block: Scope.() -> Injector<T>,
): CompoundSupplierBuilder<T, Scope> =
    CompoundSupplierBuilder(scope, paramName, type, block).also(::addSupplierBuilder)

public fun <T : Any, Scope : KiraScope> KiraScope.nullableCompound(
    scope: Scope,
    paramName: String,
    type: Type,
    isNullByDefault: Boolean = false,
    block: Scope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, Scope> =
    NullableCompoundSupplierBuilder(scope, paramName, type, isNullByDefault, block)
        .also(::addSupplierBuilder)

public class CompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var type: Type,
    block: Scope.() -> Injector<T>,
) : SupplierBuilder<T>() {
    private var injector: Injector<T> = scope.block()

    public fun modify(block: Scope.() -> Unit): CompoundSupplierBuilder<T, Scope> {
        if (!isBuilt) scope.block() else alreadyBuiltError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<T>) -> Injector<T>
    ): CompoundSupplierBuilder<T, Scope> {
        if (!isBuilt) injector = scope.block(injector) else alreadyBuiltError()
        return this
    }

    override fun provideSupplier(): Supplier<T> = CompoundSupplierImpl(
        paramName = paramName,
        suppliers = scope.collectSupplierBuilders().map { it.build() },
        type = type.notNullable(),
        injector = injector.injector,
        nullable = false,
        isNullByDefault = false,
    ) as Supplier<T>
}

public class NullableCompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var type: Type,
    public var isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>
) : SupplierBuilder<T?>() {
    private var injector: Injector<T> = scope.block()

    public fun modify(block: Scope.() -> Unit): NullableCompoundSupplierBuilder<T, Scope> {
        if (!isBuilt) scope.block() else alreadyBuiltError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<T>) -> Injector<T>
    ): NullableCompoundSupplierBuilder<T, Scope> {
        if (!isBuilt) injector = scope.block(injector) else alreadyBuiltError()
        return this
    }

    override fun provideSupplier(): Supplier<T?> = CompoundSupplierImpl(
        paramName = paramName,
        suppliers = scope.collectSupplierBuilders().map { it.build() },
        type = type.nullable(),
        injector = injector.injector,
        nullable = true,
        isNullByDefault = isNullByDefault,
    )
}

private fun alreadyBuiltError(): Nothing =
    error("SupplierBuilder was already built, modification is prohibited")

private class CompoundSupplierImpl<T : Any>(
    private val paramName: String,
    private val suppliers: List<Supplier<*>>,
    private val type: Type,
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
        Layout({
            if (!::_currentValue.isInitialized) {
                currentValue()
            } else if (_currentValue.value != null) {
                val value = injector()
                latestNonNullValue = value
                _currentValue.value = value
            }
        }) { measurables, _ ->
            if (measurables.isNotEmpty()) System.err.println(uiFromInjectorMsg)
            layout(0, 0) {}
        }

        @Suppress("NAME_SHADOWING")
        val params = params as CompoundParams?
        val compressIntoDialog =
            params != null && params.nestingLevel >= COMPRESS_INTO_DIALOG_NESTING

        if (compressIntoDialog) {
            Compressed()
            if (openDialog) CompoundDialog(params)
        } else {
            val padding = 16.dp
            Column(Modifier.padding(start = padding, top = padding, bottom = padding)) {
                Header(
                    Modifier.padding(end = padding),
                    overlineText = { TypeUi(type) },
                    text = { Text(text = paramName) },
                    end = { if (nullable) NullCheckbox() }
                )
                if (suppliers.isNotEmpty()) {
                    VerticalDividerBox(modifier = Modifier.nullOverlayModifier()) {
                        // not lazy because kira root places us in the LazyColumn
                        // and LazyColumn in LazyColumn is prohibited
                        Column {
                            suppliers.forEachIndexed { index, supplier ->
                                if (!isCompoundSupplier(supplier)) supplier.Ui(params = null)
                                else CompoundSupplierUi(params, supplier)
                                if (index != suppliers.lastIndex) Divider()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(
        modifier: Modifier = Modifier,
        overlineText: @Composable () -> Unit,
        text: @Composable () -> Unit,
        end: @Composable () -> Unit,
    ) = Row(modifier) {
        Column(Modifier.weight(1f)) {
            ProvideTextStyle(MaterialTheme.typography.overline) { overlineText() }
            ProvideTextStyle(MaterialTheme.typography.subtitle1) { text() }
        }
        Box(modifier = Modifier.padding(start = 16.dp)) { end() }
    }

    @Composable
    private fun VerticalDividerBox(modifier: Modifier, content: @Composable () -> Unit) {
        Row(modifier = modifier.height(IntrinsicSize.Min)) {
            VerticalDivider(modifier = Modifier.padding(top = 8.dp))
            content()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun Modifier.nullOverlayModifier() = composed {
        val alpha by animateFloatAsState(
            targetValue = if (_currentValue.value == null) ContentAlpha.disabled else 1f
        )
        alpha(alpha)
    }.pointerInteropFilter { _currentValue.value == null }

    @Composable
    private fun Compressed() = ListItem(
        modifier = Modifier.clickable { openDialog = true },
        overlineText = { TypeUi(type) },
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
                        overlineText = { TypeUi(type) },
                        text = { Text(text = paramName) },
                        end = { if (nullable) NullCheckbox() }
                    )
                    if (suppliers.isNotEmpty()) Divider()
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                            .nullOverlayModifier()
                    ) {
                        itemsIndexed(suppliers) { index, supplier ->
                            if (!isCompoundSupplier(supplier)) supplier.Ui(params = null)
                            else CompoundSupplierUi(params, supplier)
                            if (index != suppliers.lastIndex) Divider()
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

    private fun isCompoundSupplier(supplier: Supplier<*>): Boolean =
        supplier is CompoundSupplierImpl<*>

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
        val previousNestingLevel = params?.nestingLevel ?: 0
        supplier.Ui(params = CompoundParams(nestingLevel = previousNestingLevel + 1))
    }

    private data class CompoundParams(val nestingLevel: Int)
}

private const val COMPRESS_INTO_DIALOG_NESTING = 2
private val uiFromInjectorMsg = """
    ================================================================================================
    ================================================================================================
    =======                                                                                  =======
    =======   Emitting UI from injector of CompoundSupplier is is prohibited, it was HIDDEN  =======
    =======                                                                                  =======
    ================================================================================================
    ================================================================================================
    """.trimIndent()

@Preview(showBackground = true)
@Composable
private fun Preview() =
    KiraScope().compound("param 1", ClassType("String")) {
        boolean("bool param 1", false)
        nullableCompound("param 2", ClassType("String")) {
            boolean("bool param 1", false)
            nullableCompound("param 3", ClassType("String")) {
                boolean("bool param 1", false)
                injector { }
            }
            boolean("bool param 2", true)
            injector { }
        }
        boolean("bool param 2", true)
        injector { }
    }.build().Ui()

@Preview(showBackground = true)
@Composable
private fun NullablePreview() =
    KiraScope().nullableCompound("param name", ClassType("String")) {
        boolean("bool param 1", false)
        boolean("bool param 2", true)
        injector { }
    }.build().Ui()
