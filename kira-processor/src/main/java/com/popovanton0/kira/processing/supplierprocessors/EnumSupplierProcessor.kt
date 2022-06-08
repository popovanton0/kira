package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toTypeName

object EnumSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "EnumSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableEnumSupplierBuilder")
    private val builderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "enum")
    private val nullableBuilderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "nullableEnum")

    /**
     * ```
     * text = enum(paramName = "text")
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val resolvedType = param.resolvedType
        if ((resolvedType.declaration as KSClassDeclaration).classKind != ClassKind.ENUM_CLASS)
            return null

        val nullable = resolvedType.isMarkedNullable

        return SupplierData(
            initializer = initializer(nullable, param.name!!.asString()),
            implType = (if (nullable) nullableSupplierImplType else supplierImplType)
                .parameterizedBy(resolvedType.toTypeName())
        )
    }

    private fun initializer(nullable: Boolean, paramName: String): CodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        return buildCodeBlock {
            add("%M(paramName = %S", funName, paramName)
            if (nullable) add(", defaultValue = null")
            add(")")
        }
    }
}
