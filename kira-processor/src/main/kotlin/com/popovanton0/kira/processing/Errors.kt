package com.popovanton0.kira.processing

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier

internal object Errors {
    fun manyRootClasses(rootAnns: List<KSAnnotated>): Nothing {
        val annotatedClassNames = rootAnns.joinToString {
            val classDeclaration = it as KSClassDeclaration
            (classDeclaration.qualifiedName ?: classDeclaration.simpleName).asString()
        }
        error(
            "Multiple classes annotated with @${kiraRootAnnClass.simpleName} were detected, " +
                    "but only 1 is allowed: $annotatedClassNames"
        )
    }

    fun funWithUnicodeCharsInTheName(fullFunName: String): Nothing = error(
        "Function $fullFunName has a name that contains unicode characters that " +
                "require escaping using backticks AND @${kiraAnnClass.simpleName}" +
                ".name wasn't specified"
    )

    fun funParamWithNoName(fullFunName: String): Nothing = error(
        "One of $fullFunName's params has no name"
    )

    fun suitableProviderNotFound(
        parameterSuppliers: List<ParameterSupplier>,
        fullFunName: String
    ): String {
        val parasWithNoProviders = parameterSuppliers
            .filterIsInstance<ParameterSupplier.Empty>()
            .joinToString { it.parameter.name!!.asString() }

        return "No suitable provider was found for these parameters of the $fullFunName " +
                "function:\n$parasWithNoProviders"
    }
}