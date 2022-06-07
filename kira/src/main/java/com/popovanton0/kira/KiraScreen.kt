package com.popovanton0.kira

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.ui.DefaultHeader
import com.popovanton0.kira.ui.EarlyPreview

private class KiraViewModel(val kiraProvider: KiraProvider<*>) : ViewModel() {
    init {
        kiraProvider.kira.initialize()
    }
}

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
    val factory = remember { KiraViewModelFactory(kiraProvider, header) }
    val vm = viewModel<KiraViewModel>(factory = factory)

    Box {
        vm.kiraProvider.kira.Ui()
        EarlyPreview()
    }
}

private class KiraViewModelFactory(
    private val kiraProvider: KiraProvider<*>,
    private val header: @Composable (content: @Composable () -> Unit) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val kiraProviderWithHeader = object : KiraProvider<KiraScope> {
            override val kira: Kira<KiraScope> =
                kiraProvider.kira.modifyInjector { previousInjector ->
                    injector { header(previousInjector.injector) }
                } as Kira<KiraScope>
        }
        return KiraViewModel(kiraProviderWithHeader) as T
    }
}