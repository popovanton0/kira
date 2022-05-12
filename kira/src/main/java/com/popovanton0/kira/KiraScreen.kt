package com.popovanton0.kira

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.suppliers.compound.RootCompoundSupplierBuilder
import com.popovanton0.kira.ui.EarlyPreview

private class KiraViewModel(
    val root: RootCompoundSupplierBuilder<*>
) : ViewModel() {
    init {
        root.initialize()
    }
}

@Composable
public fun KiraScreen(root: RootCompoundSupplierBuilder<*>) {
    val vm = viewModel<KiraViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = KiraViewModel(root) as T
    })

    Box {
        vm.root.Ui()
        EarlyPreview()
    }
}
