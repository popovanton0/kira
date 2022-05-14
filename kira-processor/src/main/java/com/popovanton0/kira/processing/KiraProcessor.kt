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
                        environment.codeGenerator.createNewFile(
                            dependencies = Dependencies(true),
                            packageName = annotated.packageName.asString() +
                                    "kira_generated_suppliers",
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

        val topLevelClassSources = mutableListOf<String>()

        val processingScope = ProcessingScopeImpl(kiraAnn)
        val children = processFunction(processingScope, kiraAnn, function)

        val funName = function.qualifiedName!!.asString()

        append("public fun ${funName}RootSupplier() = $SUPPLIERS_PKG_NAME.compound.root(")
        if (kiraAnn.customization.enabled) append("\n\tscope = TODO(),\n")
        append(") {\n\t")

        appendCompoundSupplierBody(children, constructorName = funName)

        appendLine("\n}")
    }

    private inner class ProcessingScopeImpl(private val kiraAnn: Kira) : ProcessingScope {
        override fun processFunction(function: KSFunctionDeclaration): List<SupplierRenderResult> =
            processFunction(this, kiraAnn, function)
    }

    private fun processFunction(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        function: KSFunctionDeclaration
    ): List<SupplierRenderResult> {
        require(function.typeParameters.isEmpty()) { "Functions with generics are not supported" }
        require(Modifier.SUSPEND !in function.modifiers) {
            // todo delegate to the user impl of the injector
            "Suspend functions are not yet supported"
        }

        val params = collectSuitableParameters(function)

        return params.map { param ->
            if (param.type.isFunctionType || param.type.isSuspendFunctionType) {
                // todo isFunctionType delegate to the user impl of the injector
                // todo And add var to the GeneratedKiraScope
                TODO("Functional types are not yet supported")
            }

            supplierProcessors.firstNotNullOfOrNull { supplierProcessor ->
                with(supplierProcessor) { processingScope.renderSupplier(kiraAnn, param) }
            } ?: error("Unknown type: ${param.type.render()}")
        }
    }

    private fun collectSuitableParameters(function: KSFunctionDeclaration): List<Parameter> {
        val params = mutableListOf<Parameter>()
        val extensionReceiverRef = function.extensionReceiver
        val extensionReceiverType = extensionReceiverRef?.resolve()
        if (extensionReceiverType != null)
            params += Parameter(
                name = "",
                type = extensionReceiverType,
                typeRef = extensionReceiverRef,
                isExtension = true
            )

        function.parameters.mapNotNullTo(params) { param ->
            val hasDefault = param.hasDefault
            if (param.isVararg)
                if (hasDefault) return@mapNotNullTo null
                else TODO("Vararg params are not currently supported")
            val name = param.name?.asString()
                ?: if (hasDefault) return@mapNotNullTo null
                else error("Functions with unnamed params and no default values are not supported")
            val typeRef = param.type
            val type: KSType = typeRef.resolve()
            if (type.isError) {
                if (hasDefault) return@mapNotNullTo null
                else error("""Type of the param "$name" cannot be resolved""")
            }

            Parameter(name, type, typeRef)
        }
        return params
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


