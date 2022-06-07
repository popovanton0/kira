package com.popovanton0.kira.processing.supplierprocessors

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName

object StringSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "StringSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableStringSupplierBuilder")
    private val builderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "string")
    private val nullableBuilderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "nullableString")

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
            initializer = initializer(nullable, param.name!!.asString()),
            implType = if (nullable) nullableSupplierImplType else supplierImplType
        )
    }

    private fun initializer(nullable: Boolean, paramName: String): CodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        val defaultValue = if (nullable) "null" else "\"Lorem\""
        return CodeBlock.of(
            "%M(paramName = %S, defaultValue = %L)", funName, paramName, defaultValue
        )
    }
}
