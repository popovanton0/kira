package com.popovanton0.kira

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popovanton0.kira.suppliers.Kira
import com.popovanton0.kira.suppliers.base.ReflectionUsage
import com.popovanton0.kira.suppliers.base.Supplier
import com.popovanton0.kira.suppliers.compound.injector
import com.popovanton0.kira.ui.DefaultHeader
import com.popovanton0.kira.ui.ListItem
import com.popovanton0.kira.ui.SliderWithDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
public fun KiraScreen(
    kira: Kira<*>,
    header: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
    previewSettings: @Composable ColumnScope.() -> Unit = {},
) {
    var densityMultiplier by rememberSaveable { mutableStateOf(1f) }
    var fontScaleMultiplier by rememberSaveable { mutableStateOf(1f) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    BasicKiraScreen(
        kira = kira,
        header = { content ->
            DefaultHeader {
                header {
                    val currentDensity = LocalDensity.current
                    CompositionLocalProvider(
                        LocalDensity provides Density(
                            currentDensity.density * densityMultiplier,
                            currentDensity.fontScale * fontScaleMultiplier
                        )
                    ) {
                        content()
                    }
                }
            }
        },
        additionalUi = {
            item {
                ListItem(
                    modifier = Modifier.clickable { expanded = !expanded },
                    text = { Text(text = "Preview Settings") },
                    end = {
                        Icon(
                            modifier = Modifier.rotate(
                                animateFloatAsState(if (expanded) -180f else 0f).value
                            ),
                            painter = rememberVectorPainter(image = Icons.Default.ArrowDropDown),
                            contentDescription = "expand"
                        )
                    }
                )
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Column {
                        SliderWithDefaults(
                            label = "Display Scaling",
                            value = densityMultiplier,
                            onValueChange = { densityMultiplier = it },
                            isDefault = densityMultiplier == 1f,
                            onRequestDefault = { densityMultiplier = 1f }
                        )
                        SliderWithDefaults(
                            label = "Font Scaling",
                            value = fontScaleMultiplier,
                            onValueChange = { fontScaleMultiplier = it },
                            isDefault = fontScaleMultiplier == 1f,
                            onRequestDefault = { fontScaleMultiplier = 1f }
                        )
                        previewSettings()
                    }
                }
                Divider()
            }
        }
    )
}

@Composable
public fun BasicKiraScreen(
    kira: Kira<*>,
    header: @Composable (content: @Composable () -> Unit) -> Unit,
    additionalUi: LazyListScope.() -> Unit
) {
    val factory = remember {
        val kiraWithHeader = kira.modifyInjector { previousInjector ->
            injector { header(previousInjector.injector) }
        }
        KiraViewModelFactory(kiraWithHeader)
    }
    val vm = viewModel<KiraViewModel>(factory = factory)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        var supplier by remember { mutableStateOf<Supplier<Unit>?>(null) }
        /** building can be slow, especially if [ReflectionUsage] APIs were used */
        /** thus building is performed on the background thread */
        LaunchedEffect(true) {
            withContext(Dispatchers.IO) { supplier = vm.kira.build() }
        }
        if (LocalInspectionMode.current) {
            supplier = vm.kira.build()
        }
        if (supplier == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            supplier!!.Ui(additionalUi)
        }
    }
}

private class KiraViewModel(val kira: Kira<*>) : ViewModel()

private class KiraViewModelFactory(private val kira: Kira<*>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = KiraViewModel(kira) as T
}