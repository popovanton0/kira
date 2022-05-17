package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.ProcessingScope
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.FULL_SUPPLIER_INTERFACE_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierRenderResult

object EnumSupplierProcessor : SupplierProcessor {
    /**
     * ```
     * text = enum(paramName = "text")
     * ```
     */
    override fun renderSupplier(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        param: FunctionParameter,
        missesPrefix: String
    ): SupplierRenderResult? {
        if (Modifier.ENUM !in param.resolvedType.declaration.modifiers) return null
        val renderedType = param.resolvedType.render()

        val nullable = param.resolvedType.isMarkedNullable
        val paramName = param.name!!.asString()
        val sourceCode = buildString {
            if (nullable) append("nullableEnum") else append("enum")
            append("<$renderedType>(paramName = \"$paramName\"")
            if (nullable) append(", defaultValue = null")
            append(')')
        }

        val supplierImplName =
            if (nullable) FULL_NULLABLE_ENUM_SUPPLIER_NAME
            else FULL_ENUM_SUPPLIER_NAME
        val imports =
            if (nullable) "$SUPPLIERS_PKG_NAME.nullableEnum"
            else "$SUPPLIERS_PKG_NAME.enum"
        return SupplierRenderResult(
            varName = paramName,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$renderedType>",
            supplierImplType = "$supplierImplName<$renderedType>",
            imports = listOf(imports)
        )
    }

    private const val FULL_ENUM_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.EnumSupplierBuilder"
    private const val FULL_NULLABLE_ENUM_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.NullableEnumSupplierBuilder"
}

