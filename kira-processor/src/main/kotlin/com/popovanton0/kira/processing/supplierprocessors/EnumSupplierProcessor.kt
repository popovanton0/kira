package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName

object EnumSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "EnumSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableEnumSupplierBuilder")

    /**
     * ```
     * text = enum(paramName = "text")
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val resolvedType = param.resolvedType
        if (Modifier.ENUM !in resolvedType.declaration.modifiers) return null

        val nullable = resolvedType.isMarkedNullable

        return SupplierData(
            supplierInitializer = CodeBlock.of("TODO()"),
            supplierImplType = (if (nullable) nullableSupplierImplType else supplierImplType)
                .parameterizedBy(resolvedType.toTypeName())
        )
    }
}
