package com.popovanton0.kira

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.suppliers.compound.RootCompoundSupplierBuilder
import com.popovanton0.kira.ui.EarlyPreview

private class KiraViewModel<ReturnType : Any>(
    val root: RootCompoundSupplierBuilder<ReturnType, *>
) : ViewModel() {
    init {
        root.initialize()
    }
}

@Composable
public fun <ReturnType : Any> KiraScreen(root: RootCompoundSupplierBuilder<ReturnType, *>) {
    val vm = viewModel<KiraViewModel<ReturnType>>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            KiraViewModel(root) as T
    })
    Box {
        Column(Modifier.verticalScroll(rememberScrollState())) { vm.root.Ui() }
        EarlyPreview()
    }
}
