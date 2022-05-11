package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSType

data class Parameter(
    val name: String,
    val type: KSType,
    val isExtension: Boolean = false
)