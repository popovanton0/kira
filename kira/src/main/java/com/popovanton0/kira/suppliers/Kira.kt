package com.popovanton0.kira.suppliers

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.SupplierBuilder
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.Injector
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.ui.VerticalDivider

public interface KiraMisses

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
        if (!isBuilt) scope.block() else alreadyBuiltError()
        return this
    }

    public fun modifyInjector(
        block: Scope.(previousInjector: Injector<Unit>) -> Injector<Unit>
    ): Kira<Scope> {
        if (!isBuilt) injector = scope.block(injector) else alreadyBuiltError()
        return this
    }

    override fun provideSupplier(): Supplier<Unit> = RootCompoundSupplierImpl(
        suppliers = scope.collectSupplierBuilders().map { it.build() },
        injector = injector,
    )

    private fun alreadyBuiltError(): Nothing =
        error("SupplierBuilder was already built, modification is prohibited")
}

private class RootCompoundSupplierImpl(
    private val injector: Injector<Unit>,
    private val suppliers: List<Supplier<*>>
) : Supplier<Unit> {

    @Composable
    override fun currentValue() = Unit

    @Composable
    override fun Ui(params: Any?) = Box {
        val customParams = remember { params as? LazyListScope.() -> Unit }
        val isNotLandscape =
            LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE
        if (isNotLandscape) LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { injector() }
            customParams?.let { it() }
            items(suppliers) { it.Ui(); Divider() }
        }
        else Row {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            ) {
                injector()
            }
            VerticalDivider()
            LazyColumn(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                customParams?.let { it() }
                items(suppliers) { it.Ui(); Divider() }
            }
        }
    }
}
