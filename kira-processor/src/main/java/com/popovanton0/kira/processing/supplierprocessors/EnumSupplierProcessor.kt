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
import com.squareup.kotlinpoet.withIndent

object EnumSupplierProcessor : SupplierProcessor {
    private val supplierImplType = ClassName(SUPPLIERS_PKG_NAME, "OneOfManySupplierBuilder")
    private val builderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "enum")
    private val nullableBuilderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "nullableEnum")

    /**
     * ```
     * text = enum(paramName = "text", qualifiedName = "sdf.Food")
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val type = param.resolvedType
        if ((type.declaration as KSClassDeclaration).classKind != ClassKind.ENUM_CLASS)
            return null

        val nullable = type.isMarkedNullable

        return SupplierData(
            initializer = initializer(
                nullable, param.name!!.asString(), type.declaration.qualifiedName!!.asString()
            ),
            implType = supplierImplType.parameterizedBy(type.toTypeName())
        )
    }

    private fun initializer(
        nullable: Boolean,
        paramName: String,
        qualifiedName: String
    ): CodeBlock = buildCodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        addStatement("%M(", funName)
        withIndent {
            addStatement("paramName = %S,", paramName)
            addStatement("qualifiedName = %S,", qualifiedName)
        }
        addStatement(")")
    }
}
