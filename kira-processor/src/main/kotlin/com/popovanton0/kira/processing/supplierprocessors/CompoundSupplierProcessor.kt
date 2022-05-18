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
        missesPrefix: String
    ): SupplierRenderResult? {
        if (!param.isCompoundableClass()) return null

        val renderedType = param.resolvedType.makeNotNullable().render()
        val nullable = param.resolvedType.isMarkedNullable
        val paramName = param.name!!.asString()
        val imports = mutableListOf<String>()
        var misses: Misses.Class? = null

        val sourceCode = buildString {
            val supplierFunName = if (nullable) "nullableCompound" else "compound"
            imports += "$SUPPLIERS_PKG_NAME.compound.$supplierFunName"

            appendCompoundSupplierFunctionCall(
                supplierFunName = supplierFunName,
                renderedType = renderedType,
                kiraAnn = kiraAnn,
                param = param,
                nullable = nullable
            )

            val classDeclaration = param.resolvedType.declaration as KSClassDeclaration
            val className = classDeclaration.qualifiedName!!.asString()

            var topLevelClassSource = ""

            if (classDeclaration.classKind == ClassKind.OBJECT) {
                appendInjectorWithObject(className)
            } else {
                val primaryConstructor = classDeclaration.primaryConstructor!!
                if (primaryConstructor.isPrivate()) return null
                if (primaryConstructor.parameters.isEmpty()) {
                    appendInjectorWithClassWithEmptyConstructor(className)
                } else {
                    val params = primaryConstructor.parameters.map(::FunctionParameter)
                    val children: List<SupplierRenderResult?> = processingScope.processConstructor(
                        className = className,
                        parameters = params,
                        missesPrefix = "$missesPrefix.$paramName"
                    )

                    misses = generateMisses(children, params, paramName)

                    append("\t")
                    appendCompoundSupplierBody(
                        children = children,
                        constructorName = className,
                        missesPrefix = "$missesPrefix.$paramName",
                        parameters = params
                    )

                    // aggregating child imports
                    children.forEach { it?.imports?.forEach(imports::add) }
                }
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
            supplierImplType = "$supplierImplName<$renderedType>",
            misses = misses,
            imports = imports
        )
    }

    private fun ProcessingScope.processConstructor(
        className: String,
        parameters: List<FunctionParameter>,
        missesPrefix: String
    ): List<SupplierRenderResult?> {
        if (!knownClasses.add(className)) {
            val errorMsg = createCircularDependencyErrorMsg(className)
            knownClasses.clear()
            error(errorMsg)
        }
        val children: List<SupplierRenderResult?> = processFunction(parameters, missesPrefix)
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
        kiraAnn: Kira,
        param: FunctionParameter,
        nullable: Boolean
    ) {
        appendLine("$supplierFunName<$renderedType>(")
        if (kiraAnn.customization.enabled) appendLine("\tscope = TODO(),")
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
                //optional = param.hasDefault,
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
    children: List<SupplierRenderResult?>,
    constructorName: String,
    missesPrefix: String,
    parameters: List<FunctionParameter>
) {
    children.forEachIndexed { index, renderResult ->
        val parameter = parameters[index]
        val parameterName = parameter.name!!.asString()
        // todo remove val if Kira.Customization.enabled == true
        // todo append `_`s to every parameterName so that it is different from the package name
        // todo so that they will not get confused and code will always compile. add unit test
        append("val $parameterName = ")
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
            append("$varName = $varName.currentValue()")
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
