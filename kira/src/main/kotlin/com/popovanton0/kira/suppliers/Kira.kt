package com.popovanton0.kira.suppliers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.compound.KiraScope

public interface KiraProvider<Scope : KiraScope> {
    public val kira: Kira<Scope>
}

public fun kira(
    block: KiraScope.() -> Injector<Unit>,
): Kira<KiraScope> = Kira(KiraScope(), block)

public fun <Scope : KiraScope> kira(
    scope: Scope,
    block: Scope.() -> Injector<Unit>,
): Kira<Scope> = Kira(scope, block)

public class Kira<Scope : KiraScope> internal constructor(
    private val scope: Scope,
    block: Scope.() -> Injector<Unit>,
) : SupplierBuilder<Unit>() {
    private var injector: Injector<Unit> = scope.block()

    public fun modify(block: Scope.() -> Unit): Kira<Scope> {
        if (!isInitialized) scope.block() else alreadyInitializedError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<Unit>) -> Injector<Unit>
    ): Kira<Scope> {
        if (!isInitialized) injector = scope.block(injector) else alreadyInitializedError()
        return this
    }

    override fun BuildKey.build(): Supplier<Unit> = RootCompoundSupplierImpl(
        suppliers = scope.collectSuppliers().toList().onEach { it.initialize() },
        injector = injector,
    )
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) { injector() }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(suppliers) { it.Ui() }
            }
        }
    }
}
