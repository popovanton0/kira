package com.popovanton0.kira

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.ui.DefaultHeader
import com.popovanton0.kira.ui.EarlyPreview

@Composable
public fun <Scope : KiraScope> KiraScreen(
    kira: Kira<Scope>,
    header: @Composable (content: @Composable () -> Unit) -> Unit = { content ->
        DefaultHeader { content() }
    }
) {
    val kiraProvider = remember {
        object : KiraProvider<Scope> {
            override val kira: Kira<Scope> = kira
        }
    }
    KiraScreen(kiraProvider, header)
}

@Composable
public fun KiraScreen(
    kiraProvider: KiraProvider<*>,
    header: @Composable (content: @Composable () -> Unit) -> Unit = { content ->
        DefaultHeader { content() }
    }
) {
    val provider = remember {
        object : KiraProvider<KiraScope> {
            override val kira: Kira<KiraScope> = kiraProvider.kira.modifyInjector { prevInjector ->
                injector { header(prevInjector.injector) }
            } as Kira<KiraScope>
        }
    }
    Box {
        provider.kira.build().Ui()
        EarlyPreview()
    }
}
