package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter
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
    override fun ProcessingScope.renderSupplier(
        kiraAnn: Kira,
        parameter: Parameter
    ): SupplierRenderResult? = with(parameter) {
        if (Modifier.ENUM !in type.declaration.modifiers) return@with null

        val sourceCode = buildString {
            append("$SUPPLIERS_PKG_NAME.")
            if (type.isMarkedNullable) append("nullableEnum") else append("enum")
            append("(paramName = \"")
            append(name)
            append('"')
            if (type.isMarkedNullable) append(", defaultValue = null")
            append(')')
        }

        val renderedType = type.render()
        val supplierImplName =
            if (type.isMarkedNullable) FULL_NULLABLE_ENUM_SUPPLIER_NAME
            else FULL_ENUM_SUPPLIER_NAME
        return SupplierRenderResult(
            varName = name,
            sourceCode = sourceCode,
            supplierType = "$FULL_SUPPLIER_INTERFACE_NAME<$renderedType>",
            supplierImplType = "$supplierImplName<$renderedType>"
        )
    }

    private const val FULL_ENUM_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.EnumSupplierBuilder"
    private const val FULL_NULLABLE_ENUM_SUPPLIER_NAME =
        "$SUPPLIERS_PKG_NAME.NullableEnumSupplierBuilder"
}

