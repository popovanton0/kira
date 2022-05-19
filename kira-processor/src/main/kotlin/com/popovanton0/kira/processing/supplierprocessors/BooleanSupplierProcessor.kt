package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.ProcessingScope
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.FULL_SUPPLIER_INTERFACE_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierRenderResult

object BooleanSupplierProcessor : SupplierProcessor {
    /**
     * ```
     * text = boolean(paramName = "text", defaultValue = boolean)
     * ```
     */
    override fun renderSupplier(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        param: FunctionParameter,
        missesPrefix: String,
        scopeClassPrefix: String
    ): SupplierRenderResult? {
        val declaration = param.resolvedType.declaration
        val paramTypeName = declaration.qualifiedName?.asString()
        if (paramTypeName != "kotlin.Boolean" || declaration !is KSClassDeclaration)
            return null

        val nullable = param.resolvedType.isMarkedNullable
        val paramName = param.name!!.asString()
        val sourceCode = buildString {
            if (nullable) append("nullableBoolean") else append("boolean")
            append("(paramName = \"$paramName\", defaultValue = ")
            if (nullable) append("null") else append("false")
            append(')')
        }

        val renderedType = param.resolvedType.render()
        val supplierImplName =
            if (nullable) FULL_NULLABLE_BOOLEAN_SUPPLIER_NAME
            else FULL_BOOLEAN_SUPPLIER_NAME
        val imports =
            if (nullable) "$SUPPLIERS_PKG_NAME.nullableBoolean"
            else "$SUPPLIERS_PKG_NAME.boolean"
        return SupplierRenderResult(
            varName = paramName,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$renderedType>",
            supplierImplType = supplierImplName,
            imports = listOf(imports)
        )
    }

    private const val FULL_BOOLEAN_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.BooleanSupplierBuilder"
    private const val FULL_NULLABLE_BOOLEAN_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.NullableBooleanSupplierBuilder"
}

