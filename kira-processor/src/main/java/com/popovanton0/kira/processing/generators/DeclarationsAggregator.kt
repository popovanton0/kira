package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraDeclarationsAggregator
import com.popovanton0.kira.processing.KiraProcessor
import com.popovanton0.kira.processing.KiraProcessor.Companion.KIRA_ROOT_PKG_NAME
import com.popovanton0.kira.processing.KiraProcessor.Companion.kiraAnnClass
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

internal object DeclarationsAggregator {
    internal const val PACKAGE_PREFIX = "$KIRA_ROOT_PKG_NAME.aggregator"

    @OptIn(KspExperimental::class)
    internal fun extractNamesFromAggregator(aggregator: KSDeclaration) =
        aggregator.getAnnotationsByType(KiraDeclarationsAggregator::class).single()
            .qualifiedDeclarationNames.toList()

    internal fun aggregate(resolver: Resolver): KiraDeclarations {
        val originatingKSFiles = mutableListOf<KSFile>()
        val funNames = resolver
            .getSymbolsWithAnnotation(kiraAnnClass.canonicalName)
            .map { function ->
                require(function is KSFunctionDeclaration)
                function.containingFile?.let(originatingKSFiles::add)
                function.qualifiedName!!.asString()
            }
            .toList()
            .ifEmpty { return KiraDeclarations.empty }

        return KiraDeclarations(funNames, originatingKSFiles)
    }

    /**
     * @property names list of qualified names of all @[Kira]-annotated functions
     */
    internal data class KiraDeclarations(
        val names: List<String>,
        val originatingKSFiles: List<KSFile>,
    ) {
        companion object {
            val empty = KiraDeclarations(emptyList(), emptyList())
        }
    }

    @OptIn(DelicateKotlinPoetApi::class)
    internal fun aggregatorFile(funNames: List<String>, moduleName: String?): FileSpec {
        @Suppress("NAME_SHADOWING")
        val moduleName = moduleName ?: moduleName(funNames.first())
        val declarationsAggregatorName = aggregatorName(moduleName)
        return FileSpec.builder(
            declarationsAggregatorName.packageName,
            declarationsAggregatorName.simpleName
        )
            .addFileComment(
                """This file contains qualified names of all functions annotated with 
                        |@${kiraAnnClass.simpleName}. Its used to find them while processing the
                        |root module (module that contains class, annotated with 
                        |@${KiraProcessor.kiraRootAnnClass.simpleName})""".trimMargin()
            )
            .addType(
                TypeSpec.classBuilder(declarationsAggregatorName)
                    .addModifiers(KModifier.PRIVATE)
                    .addAnnotation(
                        AnnotationSpec.get(KiraDeclarationsAggregator(funNames.toTypedArray()))
                    )
                    .build()
            )
            .build()
    }

    private fun aggregatorName(moduleName: String) =
        ClassName(PACKAGE_PREFIX, "DeclarationsAggregator_$moduleName")

    /**
     * todo use `resolver.moduleName` when [https://github.com/google/ksp/issues/1015] is done
     * @param funName name of the any function in the module
     */
    private fun moduleName(funName: String): String =
        funName.substringBeforeLast('.').replace(".", "_").lowercase()

    /** [https://github.com/google/ksp/issues/1015] */
    private fun moduleName(resolver: Resolver): String {
        val moduleDescriptor = resolver::class.java
            .getDeclaredField("module")
            .apply { isAccessible = true }
            .get(resolver)
        val rawName = moduleDescriptor::class.java
            .getMethod("getName")
            .invoke(moduleDescriptor)
            .toString()
        return rawName
            .removeSurrounding("<", ">")
            .removeSuffix("_debug")
            .removeSuffix("_release")
    }
}