package com.popovanton0.kira.processing

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.generators.render

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

    class A(b: B, c: C)
    class B(c: C)
    class C(a: A)


    private fun processFunction(kiraAnn: Kira, annotated: KSFunctionDeclaration) {
        require(annotated.functionKind == FunctionKind.TOP_LEVEL) {
            // todo delegate to the user impl of the injector
            "Only top level functions are supported"
        }
        require(annotated.typeParameters.isEmpty()) {
            // todo delegate to the user impl of the injector
            "Functions with generics are not supported"
        }
        require(Modifier.SUSPEND !in annotated.modifiers) {
            // todo delegate to the user impl of the injector
            "Suspend functions are not yet supported"
        }

        val params = collectSuitableParameters(annotated)

        params[3].run { listOf(this) }.map { param ->
            if (param.type.isFunctionType || param.type.isSuspendFunctionType) {
                // todo isFunctionType delegate to the user impl of the injector
                // todo And add var to the GeneratedKiraScope
            }
            param.type.render()
        }.first().also(::error)
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


