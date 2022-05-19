package com.popovanton0.kira.suppliers.compound

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.VerticalDivider

public fun root(
    block: KiraScope.() -> Injector<Unit>,
): RootCompoundSupplierBuilder<KiraScope> {
    return RootCompoundSupplierBuilder(KiraScope(), block)
}

public fun <Scope : KiraScope> root(
    scope: Scope,
    block: Scope.() -> Injector<Unit>,
): RootCompoundSupplierBuilder<Scope> {
    return RootCompoundSupplierBuilder(scope, block)
}

public class RootCompoundSupplierBuilder<Scope : KiraScope> internal constructor(
    private val scope: Scope,
    block: Scope.() -> Injector<Unit>,
) : SupplierBuilder<Unit>() {
    private var injector: Injector<Unit> = scope.block()

    public fun modify(block: Scope.() -> Unit): RootCompoundSupplierBuilder<Scope> {
        if (!isInitialized) scope.block() else alreadyInitializedError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<Unit>) -> Injector<Unit>
    ): RootCompoundSupplierBuilder<Scope> {
        if (!isInitialized) injector = scope.block(injector) else alreadyInitializedError()
        return this
    }

    override fun BuildKey.build(): Supplier<Unit> = RootCompoundSupplierImpl(
        suppliers = scope.collectSuppliers().toList().onEach { it.initialize() },
        injector = injector,
    )
}

public fun <T : Any> KiraScope.compound(
    paramName: String,
    label: String,
    block: KiraScope.() -> Injector<T>,
): CompoundSupplierBuilder<T, KiraScope> =
    compound(KiraScope(), paramName, label, block)

public fun <T : Any> KiraScope.nullableCompound(
    paramName: String,
    label: String,
    isNullByDefault: Boolean,
    block: KiraScope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, KiraScope> =
    nullableCompound(KiraScope(), paramName, label, isNullByDefault, block)

public fun <T : Any, Scope : KiraScope> KiraScope.compound(
    scope: Scope,
    paramName: String,
    label: String,
    block: Scope.() -> Injector<T>,
): CompoundSupplierBuilder<T, Scope> =
    CompoundSupplierBuilder(scope, paramName, label, block).also(::addSupplier)

public fun <T : Any, Scope : KiraScope> KiraScope.nullableCompound(
    scope: Scope,
    paramName: String,
    label: String,
    isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>,
): NullableCompoundSupplierBuilder<T, Scope> =
    NullableCompoundSupplierBuilder(scope, paramName, label, isNullByDefault, block)
        .also(::addSupplier)

public class CompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var label: String,
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
        label = label,
        injector = injector.injector,
        nullable = false,
        isNullByDefault = false,
    ) as Supplier<T>
}

public class NullableCompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    private val scope: Scope,
    public var paramName: String,
    public var label: String,
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
        label = label,
        injector = injector.injector,
        nullable = true,
        isNullByDefault = isNullByDefault,
    )
}

private class CompoundSupplierImpl<T : Any>(
    private val paramName: String,
    private val suppliers: List<Supplier<*>>,
    private val label: String,
    private val injector: @Composable () -> T,
    private val nullable: Boolean,
    private val isNullByDefault: Boolean,
) : Supplier<T?> {

    private lateinit var _currentValue: MutableState<T?>
    private lateinit var latestNonNullValue: T

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

    @OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Ui() = Column {
        if (!::_currentValue.isInitialized) {
            currentValue()
        } else if (_currentValue.value != null) {
            val value = injector()
            latestNonNullValue = value
            _currentValue.value = value
        }
        Row {
            ListItem(
                modifier = Modifier.weight(1f),
                overlineText = { Text(text = "Type: $label") }, // TODO localize
                text = { Text(text = paramName) },
                secondaryText = {
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .run {
                                if (_currentValue.value == null) {
                                    alpha(ContentAlpha.disabled).pointerInteropFilter { true }
                                } else this
                            },
                    ) {
                        VerticalDivider()
                        Column {
                            suppliers.forEach { it.Ui() }
                        }
                    }
                },
            )

            if (nullable) Box(modifier = Modifier.padding(end = 16.dp)) {
                Checkbox(
                    label = "null",
                    checked = _currentValue.value == null,
                    onCheckedChange = { isNull ->
                        _currentValue.value = if (isNull) null else latestNonNullValue
                    }
                )
            }
        }
    }
}

private class RootCompoundSupplierImpl(
    private val injector: Injector<Unit>,
    private val suppliers: List<Supplier<*>>
) : Supplier<Unit> {

    @Composable
    override fun currentValue() = Unit

    @Composable
    override fun Ui() = BoxWithConstraints {
        if (maxWidth / maxHeight < 1) LazyColumn {
            item { injector() }
            items(suppliers) { it.Ui() }
        }
        else Row {
            Box(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) { injector() }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(suppliers) { it.Ui() }
            }
        }
    }
}
