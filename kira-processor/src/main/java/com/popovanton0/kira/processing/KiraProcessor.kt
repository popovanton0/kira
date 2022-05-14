package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.generators.render

class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val moduleName: String = environment.options.getOrElse("moduleName") { "" }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("com.popovanton0.kira.annotations.Kira")
            .forEach { annotated ->
                val kiraAnn = annotated.getKiraAnn(resolver)
                if (!kiraAnn.currentModuleIsTarget()) return@forEach
                when (annotated) {
                    is KSFunctionDeclaration -> {
                        val res = processRootFunction(resolver, kiraAnn, annotated)
                        res
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
        resolver: Resolver,
        kiraAnn: Kira,
        function: KSFunctionDeclaration
    ): String {
        require(function.functionKind == FunctionKind.TOP_LEVEL) {
            // todo delegate to the user impl of the injector
            "Only top level functions are supported"
        }
        return processFunction(resolver, kiraAnn, function).sourceCode
    }

    internal data class SupplierRenderResult(
        val varName: String,
        val typeName: String,
        val sourceCode: String,
    )

    private fun processFunction(
        resolver: Resolver,
        kiraAnn: Kira,
        function: KSFunctionDeclaration
    ): SupplierRenderResult {
        require(function.typeParameters.isEmpty()) { "Functions with generics are not supported" }
        require(Modifier.SUSPEND !in function.modifiers) {
            // todo delegate to the user impl of the injector
            "Suspend functions are not yet supported"
        }

        val params = collectSuitableParameters(function)

        val res = params.map { param ->
            if (param.type.isFunctionType || param.type.isSuspendFunctionType) {
                // todo isFunctionType delegate to the user impl of the injector
                // todo And add var to the GeneratedKiraScope
                TODO("Functional types are not yet supported")
            }

            val paramTypeName = param.type.declaration.qualifiedName!!.asString()

            when {
                paramTypeName == "kotlin.String" -> {
                    param.renderStringSupplier()
                }
                paramTypeName == "kotlin.Boolean" -> {
                    param.renderBooleanSupplier()
                }
                param.isEnum() -> {
                    param.renderEnumSupplier()
                }
                param.isCompoundableClass() -> {
                    param.renderCompoundSupplier(resolver, kiraAnn)
                }
                else -> error("Unknown type: ${param.type.render()}")
            }
        }.joinToString(separator = "\n") { it.sourceCode }
        return SupplierRenderResult(
            "varName",
            function.qualifiedName?.asString().toString(),
            sourceCode = res
        )
    }

    private fun Parameter.isCompoundableClass(): Boolean {
        val declaration = type.declaration
        if (declaration !is KSClassDeclaration) return false
        val classKind = declaration.classKind
        if (classKind == ClassKind.OBJECT) return true
        if (classKind != ClassKind.CLASS && classKind != ClassKind.ANNOTATION_CLASS) return false
        if (declaration.typeParameters.isNotEmpty()) return false
        if (declaration.primaryConstructor == null) return false
        if (declaration.isAbstract()) return false
        if (Modifier.SEALED in declaration.modifiers) return false
        return true
    }

    private fun Parameter.isEnum(): Boolean {
        return Modifier.ENUM in type.declaration.modifiers
    }

    /**
     * Used to detect cyclic dependencies. Contains full names of the processed classes while
     * recursively diving into the dependencies hierarchy.
     */
    private val knownClasses = mutableSetOf<String>()

    /**
     * ```
     * carN = nullableCompound(
     *     scope = CarScope(),
     *     paramName = "car",
     *     label = "Car",
     *     isNullByDefault = true,
     * ) {
     *    carBody()
     * }
     * ```
     */
    private fun Parameter.renderCompoundSupplier(
        resolver: Resolver,
        kiraAnn: Kira
    ): SupplierRenderResult {
        val varName = name
        val typeName = type.render()
        val sourceCode = buildString {
            append("com.popovanton0.kira.suppliers.")
            if (type.isMarkedNullable) {
                append("nullableCompound")
            } else {
                append("compound")
            }
            append("(\n\tscope = TODO(),\n\t")
            append("paramName = \"")
            append(name)
            append("\",\n\t")
            append("label = \"")
            append(typeName)
            append("\"")
            if (type.isMarkedNullable) {
                append(",\n\tdefaultValue = null")
            }
            append("\n) {")

            val classDeclaration = type.declaration as KSClassDeclaration
            val className = classDeclaration.qualifiedName!!.asString()
            when {
                classDeclaration.classKind == ClassKind.OBJECT -> {
                    append("\n\tcom.popovanton0.kira.suppliers.compound.injector {\n\t\t")
                    append(className) // object instance invocation
                    append("\n\t}")
                }
                classDeclaration.primaryConstructor!!.parameters.isEmpty() -> {
                    append("\n\tcom.popovanton0.kira.suppliers.compound.injector {\n\t\t")
                    append(className)
                    append("()") // primary constructor invocation
                    append("\n\t}")
                }
                else -> {
                    if (!knownClasses.add(className)) {
                        val errorMsg = knownClasses.joinToString(
                            prefix = "Circular dependency detected: \n\t\t",
                            separator = " -> \n\t\t",
                            postfix = " -> \n\t\t$className"
                        )
                        error(errorMsg)
                    }
                    val res =
                        processFunction(resolver, kiraAnn, classDeclaration.primaryConstructor!!)
                    knownClasses.remove(className)

                    res.sourceCode.lines().forEach {
                        append("\n\t")
                        append(it)
                    }

                    // injector generation

                    append("\n\n\tcom.popovanton0.kira.suppliers.compound.injector {\n\t\t")
                    append(className)
                    append("(\n\t\t\t") // primary constructor invocation
                    append("TODO()\n\t\t")
                    append(")")
                    append("\n\t}")
                }
            }
            append("\n}")
        }
        return SupplierRenderResult(varName, typeName, sourceCode)
    }

    /**
     * ```
     * text = enum(paramName = "text")
     * ```
     */
    private fun Parameter.renderEnumSupplier(): SupplierRenderResult {
        val varName = name
        val typeName = type.render()
        val sourceCode = buildString {
            append("val ")
            append(name)
            append(" = ")
            append("com.popovanton0.kira.suppliers.")
            if (type.isMarkedNullable) {
                append("nullableEnum")
            } else {
                append("enum")
            }
            append("(paramName = \"")
            append(name)
            append('"')
            if (type.isMarkedNullable) {
                append(", defaultValue = null")
            }
            append(')')
        }
        return SupplierRenderResult(varName, typeName, sourceCode)
    }

    /**
     * ```
     * text = boolean(paramName = "text", defaultValue = boolean)
     * ```
     */
    private fun Parameter.renderBooleanSupplier(): SupplierRenderResult {
        val varName = name
        val typeName = type.render()
        val sourceCode = buildString {
            append("com.popovanton0.kira.suppliers.")
            if (type.isMarkedNullable) {
                append("nullableBoolean")
            } else {
                append("boolean")
            }
            append("(paramName = \"")
            append(name)
            append("\", defaultValue = ")
            if (type.isMarkedNullable) {
                append("null")
            } else {
                append("false")
            }
            append(')')
        }
        return SupplierRenderResult(varName, typeName, sourceCode)
    }

    /**
     * ```
     * text = string(paramName = "text", defaultValue = "Lorem")
     * ```
     */
    private fun Parameter.renderStringSupplier(): SupplierRenderResult {
        val varName = name
        val typeName = type.render()
        val sourceCode = buildString {
            append("com.popovanton0.kira.suppliers.")
            if (type.isMarkedNullable) {
                append("nullableString")
            } else {
                append("string")
            }
            append("(paramName = \"")
            append(name)
            append("\", defaultValue = ")
            if (type.isMarkedNullable) {
                append("null")
            } else {
                append("\"Example\"")
            }
            append(')')
        }
        return SupplierRenderResult(varName, typeName, sourceCode)
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

    @OptIn(KspExperimental::class)
    private fun KSAnnotated.getKiraAnn(resolver: Resolver): Kira {
        val kiraAnnotation =
            annotations.singleOrNull { it.shortName.asString() == SHORT_KIRA_ANN_NAME }
            // null only in a rare case that there are other anns called `SHORT_KIRA_ANN_NAME`
                ?: annotations.first {
                    val fullAnnName = it.annotationType.resolve().declaration.qualifiedName!!
                    fullAnnName.asString() == FULL_KIRA_ANN_NAME
                }
        val args = kiraAnnotation.arguments
        val kiraAnn = getAnnotationsByType(Kira::class).first()
        kiraAnn.customization
        return Kira(
            customization = (args[0] as KSAnnotation).arguments.getArgValue(
                argName = "customization",
                position = 0,
                default = Kira.Customization(enabled = false, supplierImpls = false)
            ),
            targetModule = args.getArgValue(
                argName = "targetModule",
                position = 1,
                default = ""
            )
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


