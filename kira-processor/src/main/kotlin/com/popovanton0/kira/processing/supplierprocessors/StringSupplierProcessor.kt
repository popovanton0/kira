package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter
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
    override fun ProcessingScope.renderSupplier(
        kiraAnn: Kira,
        parameter: Parameter
    ): SupplierRenderResult? = with(parameter) {
        val paramTypeName = type.declaration.qualifiedName?.asString()
        if (paramTypeName != "kotlin.String" || type.declaration !is KSClassDeclaration)
            return@with null

        val nullable = type.isMarkedNullable
        val sourceCode = buildString {
            if (nullable) append("nullableString") else append("string")
            append("(paramName = \"name\", defaultValue = ")
            if (nullable) append("null") else append("\"Example\"")
            append(')')
        }

        val renderedType = type.render()
        val supplierImplName =
            if (nullable) FULL_NULLABLE_STRING_SUPPLIER_NAME
            else FULL_STRING_SUPPLIER_NAME
        val imports =
            if (nullable) "$SUPPLIERS_PKG_NAME.nullableString"
            else "$SUPPLIERS_PKG_NAME.string"
        return SupplierRenderResult(
            varName = name,
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

