package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.Misses
import com.popovanton0.kira.processing.supplierprocessors.base.ProcessingScope
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.FULL_SUPPLIER_INTERFACE_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierRenderResult

object CompoundSupplierProcessor : SupplierProcessor {
    /**
     * Used to detect cyclic dependencies. Contains full names of the processed classes while
     * recursively diving into the dependencies hierarchy.
     */
    private val knownClasses = mutableSetOf<String>()

    private fun FunctionParameter.isCompoundableClass(): Boolean {
        val declaration = resolvedType.declaration
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
    override fun renderSupplier(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        param: FunctionParameter,
        missesPrefix: String,
        scopeClassPrefix: String
    ): SupplierRenderResult? {
        if (!param.isCompoundableClass()) return null

        val renderedType = param.resolvedType.makeNotNullable().render()
        val nullable = param.resolvedType.isMarkedNullable
        val paramName = param.name!!.asString()
        val imports = mutableListOf<String>()
        var misses: Misses.Class? = null

        var scopeClassName: String? = null
        var scopeClassSource: String? = null

        val sourceCode = buildString {
            val supplierFunName = if (nullable) "nullableCompound" else "compound"
            imports += "$SUPPLIERS_PKG_NAME.compound.$supplierFunName"

            val classDeclaration = param.resolvedType.declaration as KSClassDeclaration
            val className = classDeclaration.qualifiedName!!.asString()
            val primaryConstructor = classDeclaration.primaryConstructor

            if (classDeclaration.classKind == ClassKind.OBJECT) {
                appendCompoundSupplierFunctionCall(
                    supplierFunName, renderedType, param, nullable, scopeClassName = null
                )
                appendInjectorWithObject(className)
            } else if (primaryConstructor == null) return null
            else if (primaryConstructor.isPrivate()) return null
            else if (primaryConstructor.parameters.isEmpty()) {
                appendInjectorWithClassWithEmptyConstructor(className)
            } else {
                val params = primaryConstructor.parameters.map(::FunctionParameter)
                scopeClassName = createScopeClassName(scopeClassPrefix, paramName)
                val children: List<SupplierRenderResult?> = processingScope.processConstructor(
                    className = className,
                    parameters = params,
                    missesPrefix = "$missesPrefix.$paramName",
                    scopeClassPrefix = scopeClassName!!,
                )

                if (children.all { it == null }) return null

                misses = generateMisses(children, params, paramName)

                if (kiraAnn.customization.enabled) {
                    scopeClassSource =
                        if (kiraAnn.customization.supplierImpls)
                            scopeClassWithImplsSource(imports, scopeClassName!!, params, children)
                        else
                            scopeClassSource(imports, scopeClassName!!, params, children)
                }

                appendCompoundSupplierFunctionCall(
                    supplierFunName, renderedType, param, nullable,
                    scopeClassName = if (kiraAnn.customization.enabled) scopeClassName else null
                )

                append("\t")
                appendCompoundSupplierBody(
                    kiraAnn = kiraAnn,
                    children = children,
                    constructorName = className,
                    missesPrefix = "$missesPrefix.$paramName",
                    parameters = params
                )

                // aggregating child imports
                children.forEach { it?.imports?.forEach(imports::add) }
            }
            append("\n}")
        }

        val supplierImplName =
            if (nullable) FULL_NULLABLE_COMPOUND_SUPPLIER_NAME
            else FULL_COMPOUND_SUPPLIER_NAME
        return SupplierRenderResult(
            varName = paramName,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$renderedType>",
            supplierImplType = "$supplierImplName<$renderedType, ${scopeClassName ?: "*"}>",
            scopeClassSource = scopeClassSource,
            misses = misses,
            imports = imports
        )
    }

    private fun ProcessingScope.processConstructor(
        className: String,
        parameters: List<FunctionParameter>,
        missesPrefix: String,
        scopeClassPrefix: String,
    ): List<SupplierRenderResult?> {
        if (!knownClasses.add(className)) {
            val errorMsg = createCircularDependencyErrorMsg(className)
            knownClasses.clear()
            error(errorMsg)
        }
        val children = processFunction(parameters, missesPrefix, scopeClassPrefix)
        knownClasses.remove(className)
        return children
    }

    private fun StringBuilder.appendInjectorWithClassWithEmptyConstructor(className: String) {
        append(
            """
               |    injector {
               |        $className()
               |    }
            """.trimMargin()
        )
    }

    private fun StringBuilder.appendInjectorWithObject(className: String) {
        append(
            """
               |    injector {
               |        $className
               |    }
            """.trimMargin()
        )
    }

    private fun StringBuilder.appendCompoundSupplierFunctionCall(
        supplierFunName: String,
        renderedType: String,
        param: FunctionParameter,
        nullable: Boolean,
        scopeClassName: String?,
    ) {
        appendLine("$supplierFunName<$renderedType>(")
        if (scopeClassName != null) {
            appendLine("\tscope = ${scopeClassName}(),")
        }
        appendLine("\tparamName = \"${param.name!!.asString()}\",")
        append("\tlabel = \"$renderedType\"")
        if (nullable) appendLine(",\n\tisNullByDefault = true") else appendLine()
        appendLine(") {")
    }

    private fun createCircularDependencyErrorMsg(className: String) = knownClasses.joinToString(
        prefix = "Circular dependency detected: \n\t\t",
        separator = " -> \n\t\t",
        postfix = " -> \n\t\t$className"
    )

    private const val FULL_COMPOUND_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.compound.CompoundSupplierBuilder"

    private const val FULL_NULLABLE_COMPOUND_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.compound.NullableCompoundSupplierBuilder"
}

internal fun generateMisses(
    children: List<SupplierRenderResult?>,
    params: List<FunctionParameter>,
    missParamName: String
): Misses.Class? {
    val missesList = children.zip(params).mapNotNull { (supplierRenderResult, param) ->
        if (supplierRenderResult != null) {
            val childMisses: Misses.Class? = supplierRenderResult.misses
            childMisses ?: return@mapNotNull null
        } else {
            return@mapNotNull Misses.Single(
                paramName = param.name!!.asString(),
                type = param.resolvedType.render(),
                //optional = param.hasDefault, todo handle default vars with no supplier
            )
        }
    }
    missesList.let {
        if (it.isEmpty()) {
            return null
        } else {
            return Misses.Class(paramName = missParamName, list = it)
        }
    }
}

internal fun StringBuilder.appendCompoundSupplierBody(
    kiraAnn: Kira,
    children: List<SupplierRenderResult?>,
    constructorName: String,
    missesPrefix: String,
    parameters: List<FunctionParameter>
) {
    children.forEachIndexed { index, renderResult ->
        val parameter = parameters[index]
        val parameterName = parameter.name!!.asString()
        // todo add unit test: paramName is the same as the first section of the package name
        if (kiraAnn.customization.enabled) append("this.")
        // underscore is appended in case package name has the same beginning as the paramName
        else append("val _")
        append("$parameterName = ")
        if (renderResult == null) {
            append(missesPrefix)
            append('.')
            appendLine(parameterName)
            append('\t')
        } else {
            renderResult.sourceCode.lines().forEach {
                appendLine(it)
                append('\t')
            }
        }
    }

    // injector generation
    val constructorCallBody = buildString {
        parameters.forEachIndexed { index, parameter ->
            val varName = parameter.name!!.asString()
            append("$varName = ")
            if (kiraAnn.customization.enabled) append("this.")
            // underscore is appended in case package name has the same beginning as the paramName
            else append("_")
            append("$varName.currentValue()")
            if (index != children.lastIndex) append(",\n\t\t\t")
        }
    }
    appendLine()

    append(
        """
           |    injector {
           |        $constructorName(
           |            $constructorCallBody
           |        )
           |    }
        """.trimMargin()
    )
}

internal fun createScopeClassName(scopeClassPrefix: String, paramName: String): String {
    val scopeClassNamePrefix = if (scopeClassPrefix.isEmpty()) "" else "$scopeClassPrefix."
    return "$scopeClassNamePrefix${paramName.replaceFirstChar { it.titlecaseChar() }}Scope"
}

internal fun scopeClassWithImplsSource(
    imports: MutableList<String>,
    scopeClassName: String,
    params: List<FunctionParameter>,
    children: List<SupplierRenderResult?>
) = buildString {
    imports += "$SUPPLIERS_PKG_NAME.compound.GeneratedKiraScopeWithImpls"
    imports += "$SUPPLIERS_PKG_NAME.compound.GeneratedKiraScopeWithImpls.SupplierImplsScope"
    appendLine("public class $scopeClassName : GeneratedKiraScopeWithImpls<$scopeClassName.SupplierImplsScope>() {")
    appendLine()
    params.forEach { param ->
        appendLine("\tpublic lateinit var ${param.name!!.asString()}: Supplier<${param.resolvedType.render()}>")
    }
    appendLine()

    appendLine("\toverride val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)\n")

    appendLine("\tpublic class SupplierImplsScope(private val scope: $scopeClassName): GeneratedKiraScopeWithImpls.SupplierImplsScope() {\n")

    children.forEach { renderResult ->
        with(renderResult ?: return@forEach) {
            appendLine(
                """
                    |    public var $varName: ${supplierImplType!!}
                    |        get() = scope.$varName as? $supplierImplType ?: implChanged()
                    |        set(value) { scope.$varName = value }
                    |        
                    """.trimMargin().prependIndent()
            )
        }
    }
    appendLine("\t}\n")

    append("\toverride fun collectSuppliers(): List<Supplier<*>> = listOf(")

    children.forEach { renderResult ->
        append(renderResult?.varName ?: return@forEach)
        append(", ")
    }

    appendLine(")\n")

    children.forEach { renderResult ->
        renderResult ?: return@forEach
        renderResult.scopeClassSource?.prependIndent()?.also(::appendLine) ?: return@forEach
    }

    appendLine("}")
}

internal fun scopeClassSource(
    imports: MutableList<String>,
    scopeClassName: String,
    params: List<FunctionParameter>,
    children: List<SupplierRenderResult?>
) = buildString {
    imports += "$SUPPLIERS_PKG_NAME.compound.GeneratedKiraScope"
    appendLine("public class $scopeClassName : GeneratedKiraScope() {")
    appendLine()
    params.forEach { param ->
        appendLine("\tpublic lateinit var ${param.name!!.asString()}: Supplier<${param.resolvedType.render()}>")
    }
    appendLine("\n")

    append("\toverride fun collectSuppliers(): List<Supplier<*>> = listOf(")

    children.forEach { renderResult ->
        append(renderResult?.varName ?: return@forEach)
        append(", ")
    }

    appendLine(")\n")

    children.forEach { renderResult ->
        renderResult ?: return@forEach
        renderResult.scopeClassSource?.prependIndent()?.also(::appendLine) ?: return@forEach
    }

    appendLine("}")
}