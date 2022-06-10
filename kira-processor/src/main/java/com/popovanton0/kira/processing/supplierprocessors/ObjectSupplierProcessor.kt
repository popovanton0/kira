package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.withIndent

object ObjectSupplierProcessor : SupplierProcessor {
    private val supplierImplType = ClassName(SUPPLIERS_PKG_NAME, "OneOfManySupplierBuilder")
    private val builderFunName = MemberName(SUPPLIERS_PKG_NAME, "object")
    private val nullableBuilderFunName = MemberName(SUPPLIERS_PKG_NAME, "nullableObject")

    /**
     * ```
     * ds8 = `object`(
     *     paramName = "ds8",
     *     qualifiedName = "sdf.Rock"
     *     value = Rock,
     * )
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val type = param.resolvedType
        val declaration = type.declaration as? KSClassDeclaration ?: return null
        if (declaration.classKind != ClassKind.OBJECT) return null

        val nullable = type.isMarkedNullable

        return SupplierData(
            initializer = initializer(nullable, param.name!!.asString(), declaration.toClassName()),
            implType = supplierImplType.parameterizedBy(type.makeNotNullable().toTypeName())
        )
    }

    private fun initializer(
        nullable: Boolean,
        paramName: String,
        objectName: ClassName
    ) = buildCodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        addStatement("%M(", funName)
        withIndent {
            addStatement("paramName = %S,", paramName)
            addStatement("qualifiedName = %S,", objectName)
            addStatement("value = %T,", objectName)
        }
        addStatement(")")
    }
}
