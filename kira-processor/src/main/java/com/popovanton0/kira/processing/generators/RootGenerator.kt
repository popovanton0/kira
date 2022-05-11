package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter

/*interface FunctionGenerator {
    fun generateCode()
}*/

class FunctionGenerator/*: FunctionGenerator*/ {
    /*override*/ fun generateCode(kiraAnn: Kira, function: KSFunctionDeclaration, isRoot: Boolean) {
        require(function.functionKind == FunctionKind.TOP_LEVEL) {
            // todo delegate to the user impl of the injector
            "Only top level functions are supported"
        }
        require(function.typeParameters.isEmpty()) {
            // todo delegate to the user impl of the injector
            "Functions with generics are not supported"
        }
        require(Modifier.SUSPEND !in function.modifiers) {
            // todo delegate to the user impl of the injector
            "Suspend functions are not yet supported"
        }

        val returnType = function.returnType?.resolve()
            ?: error("Error occurred during the type resolution of the function's return type")

        val params = collectSuitableParameters(function)

        val code = StringBuilder(2000)
        code.append(
            "val = "
        )
        params.forEach { param ->
            if (param.type.isFunctionType || param.type.isSuspendFunctionType) {
                // todo delegate to the user impl of the injector
                // todo And add var to the GeneratedKiraScope
                TODO("Function types are not yet supported")
            }
            //when (param.type.declaration.typeParameters.first().) {
            //    -> {}
            //    else -> {}
            //}
        }
    }

    private fun collectSuitableParameters(function: KSFunctionDeclaration): List<Parameter> {
        val params = mutableListOf<Parameter>()
        val extensionReceiverType = function.extensionReceiver?.resolve()
        if (extensionReceiverType != null)
            params += Parameter(name = "", type = extensionReceiverType, isExtension = true)

        function.parameters.mapNotNullTo(params) { param ->
            val hasDefault = param.hasDefault
            val name = param.name?.asString()
                ?: if (hasDefault) return@mapNotNullTo null
                else error("Functions with unnamed params and no default values are not supported")
            val type: KSType =
                if (param.isVararg) TODO("Vararg params are not currently supported")
                else param.type.resolve()
            if (type.isError) {
                if (hasDefault) return@mapNotNullTo null
                else error("""Type of the param "$name" cannot be resolved""")
            }

            Parameter(name, type)
        }
        return params
    }
}