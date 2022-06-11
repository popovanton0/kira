package com.popovanton0.kira

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.KiraProvider
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.base.Ui
import com.popovanton0.kira.suppliers.compound.KiraScope
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.ui.DefaultHeader
import com.popovanton0.kira.ui.EarlyPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private class KiraViewModel(val kiraProvider: KiraProvider<*>) : ViewModel()

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var supplier by remember { mutableStateOf<Supplier<Unit>?>(null) }
            /** building can be slow, especially if [ReflectionUsage] APIs were used */
            /** thus building is performed on the background thread */
        LaunchedEffect(true) {
            withContext(Dispatchers.IO) { supplier = vm.kiraProvider.kira.build() }
        }
        if (supplier == null) CircularProgressIndicator() else supplier!!.Ui()
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