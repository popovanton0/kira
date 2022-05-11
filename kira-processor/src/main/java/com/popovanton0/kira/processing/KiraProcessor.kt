@file:OptIn(KspExperimental::class)

package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.popovanton0.kira.annotations.Kira

class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val moduleName: String = environment.options.getOrElse("moduleName") { "" }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        //val neededCustomTypes = mutableListOf<KSType>()
        resolver.getSymbolsWithAnnotation("com.popovanton0.kira.annotations.Kira")
            .forEach { annotated ->
                val kiraAnn = annotated.getKiraAnn()
                if (!kiraAnn.currentModuleIsTarget()) return@forEach
                when (annotated) {
                    is KSFunctionDeclaration -> processFunction(kiraAnn, annotated)
                    is KSClassDeclaration -> TODO()
                    else -> error("@Kira annotation is applicable only to classes and functions")
                }
            }
        return emptyList()
    }

    private fun processFunction(kiraAnn: Kira, annotated: KSFunctionDeclaration) {
        annotated.parameters.mapNotNull {
            val name = it.name?.asString()
                ?: if (it.hasDefault) return@mapNotNull null
                else error("Functions with unnamed params are not supported")
            val type: KSType
            // todo if (it.isVararg) type = Array<it.type>; *arrayOf
            type = it.type.resolve().starProjection()

            Parameter(name, type)
        }.forEach {

        }
    }

    /**
     * Whether [Kira.targetModule] is the same as the module we are currently processing
     */
    private fun Kira.currentModuleIsTarget(): Boolean = when {
        targetModule.isBlank() -> true
        targetModule.isNotBlank() && targetModule == moduleName -> true
        else -> false
    }

    private fun KSAnnotated.getKiraAnn(): Kira {
        val kiraAnnotation =
            annotations.singleOrNull { it.shortName.asString() == SHORT_KIRA_ANN_NAME }
            // null only in a rare case that there are other anns called `SHORT_KIRA_ANN_NAME`
                ?: annotations.first {
                    val fullAnnName = it.annotationType.resolve().declaration.qualifiedName!!
                    fullAnnName.asString() == FULL_KIRA_ANN_NAME
                }
        val args = kiraAnnotation.arguments
        return Kira(
            supplierImpls = args.getArgValue("supplierImpls", position = 0, default = true),
            targetModule = args.getArgValue("targetModule", position = 1, default = "")
        )
    }

    private fun <T> List<KSValueArgument>.getArgValue(
        argName: String,
        position: Int,
        default: T
    ): T = find { it.name?.asString() == argName }?.value as? T ?: getOrNull(position)?.value as? T
    ?: default
}

private const val FULL_KIRA_ANN_NAME = "com.popovanton0.kira.annotations.Kira"
private const val SHORT_KIRA_ANN_NAME = "Kira"


