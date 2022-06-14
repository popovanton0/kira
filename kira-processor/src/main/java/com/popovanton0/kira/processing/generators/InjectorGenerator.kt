package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.processing.KiraFunction
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.withIndent

internal class InjectorGenerator(kiraFunction: KiraFunction) {
    private val function = kiraFunction.function
    /**
     * @param condition if true, injector cannot be generated
     * @param reasonMsg if [condition] is true, this msg is inserted in the generated code to let
     * the user know, why injector wasn't generated
     */
    private data class Reason(
        val name: String,
        val condition: Boolean,
        val reasonMsg: String,
    )

    private val varargParams = kiraFunction.function.parameters
        .filterNot { kiraFunction.kiraAnn.useDefaultValueForParams.contains(it.name!!.asString()) }
        .filter { it.isVararg }

    private val reasons = listOf(
        Reason(
            name = "hasVarargParams",
            condition = varargParams.isNotEmpty(),
            reasonMsg = "Functions with vararg params are not supported",
        ),
        Reason(
            name = "hasExtensionReceiver",
            condition = function.extensionReceiver != null,
            reasonMsg = "Function has an extension receiver, which are not yet supported." +
                    " Provide it manually",
        ),
        Reason(
            name = "hasTypeParameters",
            condition = function.typeParameters.isNotEmpty(),
            reasonMsg = "Functions with generics are not supported. Provide types manually",
        ),
        Reason(
            name = "notTopLevel",
            condition = function.functionKind != FunctionKind.TOP_LEVEL,
            reasonMsg = "Function is not top level, thus containing class instance is needed",
        ),
        Reason(
            name = "private",
            condition = function.isPrivate(),
            reasonMsg = "Function is private, thus it cannot be called without using reflection",
        ),
        Reason(
            name = "suspend",
            condition = Modifier.SUSPEND in function.modifiers,
            reasonMsg = "Suspend functions cannot be called without a coroutine context",
        ),
    )

    val skip = reasons.any { it.condition }
    val noInjectorReasonMsg = reasons
        .filter { it.condition }
        .joinToString(separator = ";\n") { " - ${it.reasonMsg}" }

    fun injectorFunctionCall(
        kiraProviderName: ClassName,
        parameterSuppliers: List<ParameterSupplier>,
        funName: MemberName
    ) = buildCodeBlock {
        addStatement("%M(", funName)
        withIndent {
            parameterSuppliers.forEach { parameterSupplier ->
                val paramName = parameterSupplier.parameter.name!!.asString()
                val provided = parameterSupplier is ParameterSupplier.Provided
                if (provided) addStatement("%N = %N.build().currentValue(),", paramName, paramName)
                else addStatement(
                    "%N = this@%T.misses.%N.build().currentValue(),",
                    paramName,
                    kiraProviderName,
                    paramName
                )
            }
        }
        addStatement(")")
    }
}