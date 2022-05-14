package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter
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
    override fun ProcessingScope.renderSupplier(
        kiraAnn: Kira,
        parameter: Parameter
    ): SupplierRenderResult? = with(parameter) {
        if (!parameter.isCompoundableClass()) return@with null

        val typeName = type.render()
        val nullable = type.isMarkedNullable
        val sourceCode = buildString {
            val supplierFunName = if (nullable) "nullableCompound" else "compound"

            appendLine("$SUPPLIERS_PKG_NAME.compound.$supplierFunName(")
            if (kiraAnn.customization.enabled) appendLine("\tscope = TODO(),")
            appendLine("\tparamName = \"$name\",")
            append("\tlabel = \"$typeName\"")
            if (nullable) appendLine(",\n\tdefaultValue = null") else appendLine()
            appendLine(") {")

            val classDeclaration = type.declaration as KSClassDeclaration
            val className = classDeclaration.qualifiedName!!.asString()
            var topLevelClassSource = ""
            when {
                classDeclaration.classKind == ClassKind.OBJECT -> append(
                    """
                       |    $SUPPLIERS_PKG_NAME.compound.injector {
                       |        $className
                       |    }
                    """.trimMargin()
                )
                classDeclaration.primaryConstructor!!.parameters.isEmpty() -> append(
                    """
                       |    $SUPPLIERS_PKG_NAME.compound.injector {
                       |        $className()
                       |    }
                    """.trimMargin()
                )
                else -> constructorWithParams(className, classDeclaration, this)
            }
            append("\n}")
        }

        val supplierImplName =
            if (nullable) FULL_NULLABLE_COMPOUND_SUPPLIER_NAME
            else FULL_COMPOUND_SUPPLIER_NAME
        return SupplierRenderResult(
            varName = name,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$typeName>",
            supplierImplType = "$supplierImplName<$typeName>"
        )
    }

    private fun ProcessingScope.constructorWithParams(
        className: String,
        classDeclaration: KSClassDeclaration,
        stringBuilder: StringBuilder
    ) = with(stringBuilder) {
        if (!knownClasses.add(className)) {
            val errorMsg = createCircularDependencyErrorMsg(className)
            knownClasses.clear()
            error(errorMsg)
        }
        val children = processFunction(classDeclaration.primaryConstructor!!)
        knownClasses.remove(className)

        append("\t")
        appendCompoundSupplierBody(children, className)
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

public fun StringBuilder.appendCompoundSupplierBody(
    children: List<SupplierRenderResult>,
    constructorName: String
) {
    children.forEach { result ->
        append("val ${result.varName} = ")
        result.sourceCode.lines().forEach {
            appendLine(it); append('\t')
        }
    }

    // injector generation
    val constructorCallBody = buildString {
        children.forEachIndexed { index, result ->
            val varName = result.varName
            append("$varName = $varName.currentValue()")
            if (index != children.lastIndex) append(",\n\t\t\t")
        }
    }
    appendLine()

    append(
        """
               |    $SUPPLIERS_PKG_NAME.compound.injector {
               |        $constructorName(
               |            $constructorCallBody
               |        )
               |    }
            """.trimMargin()
    )
}
