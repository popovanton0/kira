package com.popovanton0.kira.processing

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

        val s: Function1<Unit, Unit> = { _ -> }
        params[2].run { listOf(this) }.map { param ->
            if (param.type.isFunctionType || param.type.isSuspendFunctionType) {
                // todo delegate to the user impl of the injector ()
                // todo And add var to the GeneratedKiraScope

                "Function types are not yet supported: " + param.type
                param.type.generateCode()
            }
            //when (param.type.declaration.typeParameters.first().) {
            //    -> {}
            //    else -> {}
            //}
            else "null"
        }.first().also(::error)
    }

    private fun KSType.generateCode(): String {
        if (isFunctionType || isSuspendFunctionType) {
            // @Composable ((Char,Unit) -> Unit,Int,Unit,) -> Unit
            // @Composable (@Composable Char.()->Unit).(Int) -> Unit
            val isSuspend = declaration.simpleName.asString().startsWith("Suspend")
            val hasExtension = annotations.any {
                it.qualifiedName() == "kotlin.ExtensionFunctionType"
            }
            val isComposable = annotations.any {
                it.qualifiedName() == "androidx.compose.runtime.Composable"
            }
            //this.arguments.joinToString(prefix = "<", postfix = ">") { it.type!!.resolve().generateCode() }
            val code = buildString {
                if (isComposable) append("@androidx.compose.runtime.Composable ")
                if (isSuspend) append("suspend ")
                // assume that there is no explicitly specified variance in
                // function types like `Function1<Char, Unit>`
                val args = arguments.map { it.type!!.resolve().generateCode() }

                if (hasExtension) append("${args[0]}.")
                append("(")
                args.forEachIndexed { index, arg ->
                    // first arg type is the type of the extension
                    if (index == 0 && hasExtension) return@forEachIndexed
                    append(arg)
                    append(',')
                    // last arg type is the return type
                    if (index == args.lastIndex) return@forEachIndexed
                }
                append(") -> ")
                append(args.last()) // Function always has the return type
            }
            return code
        }
        return this.declaration.qualifiedName!!.asString()
    }

    /**
     * Calling this method is expensive because it calls [KSTypeReference.resolve]
     */
    private fun KSAnnotation.qualifiedName() =
        annotationType.resolve().declaration.qualifiedName?.asString()

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


