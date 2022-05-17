package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.ProcessingScope
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.FULL_SUPPLIER_INTERFACE_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierRenderResult

object StringSupplierProcessor : SupplierProcessor {
    /**
     * ```
     * text = string(paramName = "text", defaultValue = "Lorem")
     * ```
     */
    override fun renderSupplier(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        param: FunctionParameter,
        missesPrefix: String
    ): SupplierRenderResult? {
        val declaration = param.resolvedType.declaration
        val paramTypeName = declaration.qualifiedName?.asString()
        if (paramTypeName != "kotlin.String" || declaration !is KSClassDeclaration)
            return null

        val nullable = param.resolvedType.isMarkedNullable
        val paramName = param.name!!.asString()
        val sourceCode = buildString {
            if (nullable) append("nullableString") else append("string")
            append("(paramName = \"paramName\", defaultValue = ")
            if (nullable) append("null") else append("\"Example\"")
            append(')')
        }

        val renderedType = param.resolvedType.render()
        val supplierImplName =
            if (nullable) FULL_NULLABLE_STRING_SUPPLIER_NAME
            else FULL_STRING_SUPPLIER_NAME
        val imports =
            if (nullable) "$SUPPLIERS_PKG_NAME.nullableString"
            else "$SUPPLIERS_PKG_NAME.string"
        return SupplierRenderResult(
            varName = paramName,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$renderedType>",
            supplierImplType = "$supplierImplName<$renderedType>",
            imports = listOf(imports)
        )
    }

    private const val FULL_STRING_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.StringSupplierBuilder"
    private const val FULL_NULLABLE_STRING_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.NullableStringSupplierBuilder"
}

