package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference

data class Parameter(
    val name: String,
    val type: KSType,
    val typeRef: KSTypeReference,
    val isExtension: Boolean = false
)