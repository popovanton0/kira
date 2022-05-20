package com.popovanton0.kira.processing.supplierprocessors

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

object BooleanSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "BooleanSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableBooleanSupplierBuilder")

    /**
     * ```
     * text = boolean(paramName = "text", defaultValue = boolean)
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration
        val paramTypeName = declaration.qualifiedName?.asString()
        if (paramTypeName != "kotlin.Boolean") return null

        val nullable = param.resolvedType.isMarkedNullable

        return SupplierData(
            functionParameter = param,
            supplierInitializer = CodeBlock.of("TODO()"),
            supplierImplType = if (nullable) nullableSupplierImplType else supplierImplType
        )
    }
}

