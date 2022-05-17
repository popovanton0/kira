package com.popovanton0.kira.processing

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.popovanton0.kira.annotations.Customization
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.supplierprocessors.*
import com.popovanton0.kira.processing.supplierprocessors.base.ProcessingScope
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierRenderResult

class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val moduleName: String = environment.options.getOrElse("moduleName") { "" }
    private val supplierProcessors: List<SupplierProcessor> = listOf(
        StringSupplierProcessor,
        BooleanSupplierProcessor,
        EnumSupplierProcessor,
        CompoundSupplierProcessor,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("com.popovanton0.kira.annotations.Kira")
            .forEach { annotated ->
                val kiraAnn = annotated.getKiraAnn()
                if (!kiraAnn.currentModuleIsTarget()) return@forEach
                when (annotated) {
                    is KSFunctionDeclaration -> {
                        val res = processRootFunction(kiraAnn, annotated).replace("\t", "    ")
                        val pkgName = annotated.packageName.asString()
                        environment.codeGenerator.createNewFile(
                            dependencies = Dependencies(true),
                            packageName = ("$pkgName.kira_generated_suppliers").removePrefix("."),
                            fileName = annotated.qualifiedName!!.asString(),
                        ).use {
                            it.writer().use { it.write(res) }
                        }
                    }
                    is KSClassDeclaration -> {
                        TODO("Auto-generation of suppliers for classes is not yet supported")
                        when {
                            Modifier.DATA in annotated.modifiers -> TODO()
                            Modifier.SEALED in annotated.modifiers -> TODO()
                        }
                    }
                    else -> error("@Kira annotation is applicable only to classes and functions")
                }
            }
        return emptyList()
    }

    private fun processRootFunction(
        kiraAnn: Kira,
        function: KSFunctionDeclaration
    ): String = buildString {
        require(function.functionKind == FunctionKind.TOP_LEVEL) {
            // todo delegate to the user impl of the injector
            "Only top level functions are supported"
        }
        require(function.typeParameters.isEmpty()) { "Functions with generics are not supported" }
        require(Modifier.SUSPEND !in function.modifiers) {
            // todo delegate to the user impl of the injector
            "Suspend functions are not yet supported"
        }

        val topLevelClassSources = mutableListOf<String>()

        val processingScope = ProcessingScopeImpl(kiraAnn)
        val params = function.parameters.map(::FunctionParameter)
        val children = processFunction(processingScope, kiraAnn, params, "misses")

        val funName = function.qualifiedName!!.asString()
        val funSimpleName = function.simpleName.asString()

        appendLine("// This code is autogenerated. Please, do not edit this file.")
        val pkgName =
            "${function.packageName.asString()}.kira_generated_suppliers".removePrefix(".")
        appendLine("package $pkgName")
        appendLine()
        appendImportStatements(children)
        appendLine()

        val missClassesSourceCode: String? =
            generateMisses(children, params, missParamName = funSimpleName)
                ?.run(::generateMissClass)

        missClassesSourceCode?.let(::appendLine)

        append("public fun ${funSimpleName}RootSupplier(")
        if (missClassesSourceCode != null) {
            append("\n\tmisses: ${funSimpleName}Misses\n")
        }
        append(") = $SUPPLIERS_PKG_NAME.compound.root(")
        if (kiraAnn.customization.enabled) append("\n\tscope = TODO(),\n")
        append(") {\n\t")

        appendCompoundSupplierBody(
            children,
            constructorName = funName,
            "misses",
            params
        )

        appendLine("\n}")
    }

    private fun generateMissClass(
        miss: Misses.Class
    ): String = buildString {
        val className = "${miss.paramName}Misses"
        appendLine("public data class $className(")
        miss.list.forEach { miss ->
            when (miss) {
                is Misses.Class -> {
                    appendLine("\tval ${miss.paramName}: $className.${miss.paramName}Misses,")
                }
                is Misses.Single -> {
                    appendLine("\tval ${miss.paramName}: Supplier<${miss.type}>,")
                }
            }
        }
        appendLine(") {")

        miss.list.filterIsInstance<Misses.Class>().forEach { miss ->
            generateMissClass(miss).lines().forEach { line ->
                append("\t")
                append(line)
                appendLine()
            }
        }

        appendLine("}")
    }

    private fun StringBuilder.appendImportStatements(children: List<SupplierRenderResult?>) {
        appendLine("import com.popovanton0.kira.suppliers.base.Supplier")
        children.forEach {
            it?.imports?.forEach { import ->
                append("import ")
                appendLine(import)
            }
        }
    }

    private inner class ProcessingScopeImpl(private val kiraAnn: Kira) : ProcessingScope {
        override fun processFunction(
            params: List<FunctionParameter>,
            missesPrefix: String
        ): List<SupplierRenderResult?> = processFunction(this, kiraAnn, params, missesPrefix)
    }

    private fun processFunction(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        params: List<FunctionParameter>,
        missesPrefix: String,
    ): List<SupplierRenderResult?> = params.map { param ->
        supplierProcessors.firstNotNullOfOrNull { supplierProcessor ->
            with(supplierProcessor) {
                renderSupplier(processingScope, kiraAnn, param, missesPrefix)
            }
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
            customization = (args[0].value as KSAnnotation).asCustomizationAnn(),
            targetModule = args.getArgValue(
                argName = "targetModule",
                position = 1,
                default = ""
            )
        )
    }

    private fun KSAnnotation.asCustomizationAnn(): Customization {
        return Customization(
            enabled = arguments.getArgValue(
                argName = "customization",
                position = 0,
                default = false
            ),
            supplierImpls = arguments.getArgValue(
                argName = "supplierImpls",
                position = 1,
                default = false
            ),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> List<KSValueArgument>.getArgValue(
        argName: String,
        position: Int,
        default: T
    ): T = find { it.name?.asString() == argName }?.value as? T ?: getOrNull(position)?.value as? T
    ?: default
}

private const val FULL_KIRA_ANN_NAME = "com.popovanton0.kira.annotations.Kira"
private const val SHORT_KIRA_ANN_NAME = "Kira"


