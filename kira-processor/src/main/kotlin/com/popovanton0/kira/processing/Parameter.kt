package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter

class FunctionParameter(val parameter: KSValueParameter) : KSValueParameter by parameter {
    val resolvedType: KSType = parameter.type.resolve()
}