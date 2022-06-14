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
import com.popovanton0.kira.processing.sha256
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
     * @param names list of qualified names of all @[Kira]-annotated functions
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
    internal fun aggregatorFile(resolver: Resolver, funNames: List<String>): FileSpec {
        val moduleName = uniqueModuleIdentifier(resolver, funNames)
        val className = aggregatorClassName(moduleName)
        return FileSpec.builder(className.packageName, className.simpleName)
            .addFileComment(
                """This file contains qualified names of all functions annotated with 
                        |@${kiraAnnClass.simpleName} in this module. Its used to find them while 
                        |processing the root module (module that contains the class, annotated with 
                        |@${KiraProcessor.kiraRootAnnClass.simpleName})""".trimMargin()
            )
            .addType(
                TypeSpec.classBuilder(className)
                    .addModifiers(KModifier.PRIVATE)
                    .addAnnotation(
                        AnnotationSpec.get(KiraDeclarationsAggregator(funNames.toTypedArray()))
                    )
                    .build()
            )
            .build()
    }

    /**
     * Hashes [suffix], so that [illegal characters](https://github.com/JetBrains/kotlin/blob/master/compiler/frontend.java/src/org/jetbrains/kotlin/resolve/jvm/checkers/JvmSimpleNameBacktickChecker.kt)
     * from the [moduleName] don't break the build
     */
    private fun aggregatorClassName(suffix: String) =
        ClassName(PACKAGE_PREFIX, "DeclarationsAggregator_${sha256(suffix)}")

    private fun uniqueModuleIdentifier(resolver: Resolver, funNames: List<String>) =
        runCatching { moduleName(resolver) }
            .getOrElse { uniqueModuleIdentifier(funNames) }

    /** assuming that different modules have a unique set of @Kira-annotated function names */
    private fun uniqueModuleIdentifier(funNames: List<String>): String {
        val builder = StringBuilder(funNames.size * AVERAGE_FUN_NAME_LENGTH)
        funNames.joinTo(builder)
        return sha256(builder.toString())
    }

    /**
     * todo use `resolver.moduleName` when [https://github.com/google/ksp/issues/1015] is done
     * @return gradle module name
     */
    private fun moduleName(resolver: Resolver): String {
        val moduleDescriptor = resolver::class.java
            .getDeclaredField("module")
            .apply { isAccessible = true }
            .get(resolver)
        val rawName = moduleDescriptor::class.java
            .getMethod("getName")
            .invoke(moduleDescriptor)
            .toString()
        return rawName.removeSurrounding("<", ">")
    }

    private const val AVERAGE_FUN_NAME_LENGTH = 80
}