package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter

class FunctionParameter(val parameter: KSValueParameter) : KSValueParameter by parameter {
    val resolvedType: KSType = parameter.type.resolve()

    override fun equals(other: Any?): Boolean = parameter == other
    override fun hashCode(): Int = parameter.hashCode()
    override fun toString(): String = parameter.toString()
}