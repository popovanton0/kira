package com.popovanton0.kira.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference

class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(FULL_KIRA_ANN_NAME).forEach { annotated ->
            val functionDeclaration = annotated as KSFunctionDeclaration

            // String.() -> Unit
            val functionalType = functionDeclaration.parameters.first().type.resolve()

            // Ann was there
            check(
                functionalType.toString() ==
                        "[@kotlin.ExtensionFunctionType] Function1<String, Unit>"
            )
            // but it disappeared :(
            val annotations = functionalType.annotations.toList()
            val hasExtension = annotations.any {
                it.qualifiedName() == "kotlin.ExtensionFunctionType"
            } or annotations.isNotEmpty()

            check(hasExtension) { "Bug!" }
        }
        return emptyList()
    }

    /**
     * Calling this method is expensive because it calls [KSTypeReference.resolve]
     */
    private fun KSAnnotation.qualifiedName() =
        annotationType.resolve().declaration.qualifiedName?.asString()
}

private const val FULL_KIRA_ANN_NAME = "com.popovanton0.kira.annotations.Kira"
