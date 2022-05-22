package com.popovanton0.kira.processing.supplierprocessors

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

object StringSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "StringSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableStringSupplierBuilder")

    /**
     * ```
     * text = string(paramName = "text", defaultValue = "Lorem")
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration
        val paramTypeName = declaration.qualifiedName?.asString()
        if (paramTypeName != "kotlin.String") return null

        val nullable = param.resolvedType.isMarkedNullable

        return SupplierData(
            supplierInitializer = CodeBlock.of("TODO()"),
            supplierImplType = if (nullable) nullableSupplierImplType else supplierImplType
        )
    }
}
