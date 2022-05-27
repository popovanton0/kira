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
import com.popovanton0.kira.ui.EarlyPreview

private class KiraViewModel(val kiraProvider: KiraProvider<*>) : ViewModel() {
    init {
        kiraProvider.kira.initialize()
    }
}

@Composable
public fun <Scope : KiraScope> KiraScreen(kira: Kira<Scope>) {
    val kiraProvider = remember {
        object : KiraProvider<Scope> {
            override val kira: Kira<Scope> = kira
        }
    }
    KiraScreen(kiraProvider)
}

@Composable
public fun KiraScreen(kiraProvider: KiraProvider<*>) {
    val vm = viewModel<KiraViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            KiraViewModel(kiraProvider) as T
    })

    Box {
        vm.kiraProvider.kira.Ui()
        EarlyPreview()
    }
}
