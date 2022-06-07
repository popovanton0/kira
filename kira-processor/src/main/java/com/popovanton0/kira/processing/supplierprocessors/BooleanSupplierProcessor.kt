package com.popovanton0.kira.processing.supplierprocessors

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName

object BooleanSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "BooleanSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableBooleanSupplierBuilder")
    private val builderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "boolean")
    private val nullableBuilderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "nullableBoolean")

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
            initializer = initializer(nullable, param.name!!.asString()),
            implType = if (nullable) nullableSupplierImplType else supplierImplType
        )
    }

    private fun initializer(nullable: Boolean, paramName: String): CodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        val defaultValue = if (nullable) "null" else "false"
        return CodeBlock.of(
            "%M(paramName = %S, defaultValue = %L)", funName, paramName, defaultValue
        )
    }
}

