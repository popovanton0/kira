package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.popovanton0.kira.annotations.Kira

internal data class KiraFunction(
    val function: KSFunctionDeclaration,
    val kiraAnn: Kira,
)