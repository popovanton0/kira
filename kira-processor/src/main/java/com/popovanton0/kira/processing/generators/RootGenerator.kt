package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter

/*interface FunctionGenerator {
    fun generateCode()
}*/

class FunctionGenerator/*: FunctionGenerator*/ {
    /*override*/ fun generateCode(kiraAnn: Kira, function: KSFunctionDeclaration, isRoot: Boolean) {
        require(function.functionKind == FunctionKind.TOP_LEVEL) {
            "Only top level functions are supported"
        }
        require(function.typeParameters.isEmpty()) { "Functions with generics are not supported" }

        val params = function.parameters . mapNotNull {
            val name = it.name?.asString()
                ?: if (it.hasDefault) return@mapNotNull null
                else error("Functions with unnamed params are not supported")
            val type: KSType
            // todo if (it.isVararg) type = Array<it.type>; *arrayOf
            type = it.type.resolve().starProjection()

            Parameter(name, type)
        }
        val code = StringBuilder(2000)
        code.append(
            "val = "
        )
        params.forEach {

        }
    }
}