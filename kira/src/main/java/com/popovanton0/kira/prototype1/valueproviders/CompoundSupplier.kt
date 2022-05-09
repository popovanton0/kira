package com.popovanton0.kira.prototype1.valueproviders

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.popovanton0.kira.prototype1.Supplier
import com.popovanton0.kira.prototype1.SupplierBuilder
import com.popovanton0.kira.ui.Checkbox
import com.popovanton0.kira.ui.VerticalDivider

public open class KiraScope {
    internal val suppliers = mutableListOf<Supplier<*>>()

    public fun <T> injector(block: @Composable () -> T): Injector<T> = Injector(block)

    public fun addSupplier(supplier: SupplierBuilder<*>) {
        suppliers.add(supplier)
    }
}

/**
 * DO NOT use in your own code, implementations of this class SHOULD only be generated automatically
 */
public abstract class GeneratedKiraScope<ReassignScope : Any>(protected val reassignScope: ReassignScope) : KiraScope() {
    public fun reassign(block: ReassignScope.() -> Unit): Unit = reassignScope.block()

    public abstract fun collectSuppliers(): List<Supplier<*>>
}

public class Injector<T> internal constructor(internal val injector: @Composable () -> T)

public fun <T : Any, Scope : KiraScope> root(
    scope: Scope,
    block: Scope.() -> Injector<T>,
): RootCompoundSupplierBuilder<T, Scope> = RootCompoundSupplierBuilder(scope, block)

public class RootCompoundSupplierBuilder<T : Any, Scope : KiraScope> internal constructor(
    public val scope: Scope,
    block: Scope.() -> Injector<T>,
) : SupplierBuilder<T>() {
    private val injector: Injector<T> = scope.block()
    override fun build(key: BuildKey): Supplier<T> {
        val suppliers = scope.suppliers
        if (scope is GeneratedKiraScope<*>) {
            suppliers.clear()
            suppliers.addAll(scope.collectSuppliers())
        }
        suppliers.forEach { it.initialize() }
        return CompoundSupplierImpl(
            paramName = "",
            suppliers = suppliers,
            label = "",
            injector = injector.injector,
            nullable = false,
            isNullByDefault = false,
            isRoot = true,
        ) as Supplier<T>
    }
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
    public val scope: Scope,
    public var paramName: String,
    public var label: String,
    block: Scope.() -> Injector<T>,
    private val isRoot: Boolean = false,
) : SupplierBuilder<T>() {
    private val injector: Injector<T> = scope.block()
    override fun build(key: BuildKey): Supplier<T> {
        val suppliers = scope.suppliers
        if (scope is GeneratedKiraScope<*>) {
            suppliers.clear()
            suppliers.addAll(scope.collectSuppliers())
        }
        suppliers.forEach { it.initialize() }
        return CompoundSupplierImpl(
            paramName = paramName,
            suppliers = suppliers,
            label = label,
            injector = injector.injector,
            nullable = false,
            isNullByDefault = false,
            isRoot = isRoot,
        ) as Supplier<T>
    }
}

public class NullableCompoundSupplierBuilder<T : Any, Scope : KiraScope>
internal constructor(
    public val scope: Scope,
    public var paramName: String,
    public var label: String,
    public var isNullByDefault: Boolean,
    block: Scope.() -> Injector<T>
) : SupplierBuilder<T?>() {
    private val injector: Injector<T> = scope.block()
    override fun build(key: BuildKey): Supplier<T?> {
        val suppliers = scope.suppliers
        if (scope is GeneratedKiraScope<*>) {
            suppliers.clear()
            suppliers.addAll(scope.collectSuppliers())
        }
        suppliers.forEach { it.initialize() }
        return CompoundSupplierImpl(
            paramName = paramName,
            suppliers = suppliers,
            label = label,
            injector = injector.injector,
            nullable = true,
            isNullByDefault = isNullByDefault,
        )
    }
}

private class CompoundSupplierImpl<T : Any>(
    private val paramName: String,
    private val suppliers: List<Supplier<*>>,
    private val label: String,
    private val injector: @Composable () -> T,
    private val nullable: Boolean,
    private val isNullByDefault: Boolean,
    private val isRoot: Boolean = false,
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
        if (!::_currentValue.isInitialized) currentValue()
        else if (_currentValue.value != null) {
            val value = injector()
            latestNonNullValue = value
            _currentValue.value = value
        }
        Row {
            if (isRoot) {
                Column {
                    suppliers.forEach { it.Ui() }
                }
            } else ListItem(
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
