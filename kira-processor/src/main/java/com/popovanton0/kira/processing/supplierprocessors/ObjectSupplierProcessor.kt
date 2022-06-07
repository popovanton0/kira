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
    private val supplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "CompoundSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "NullableCompoundSupplierBuilder")
    private val kiraScope =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "KiraScope")
    private val builderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "singleValue")
    private val nullableBuilderFunName =
        MemberName(SUPPLIERS_PKG_NAME, "nullableSingleValue")

    /**
     * ```
     * ds8 = nullableSingleValue(
     *     paramName = "ds8",
     *     value = Rock,
     *     typeName = "sdf1.Rock",
     *     nullByDefault = true
     * )
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration as? KSClassDeclaration ?: return null
        if (declaration.classKind != ClassKind.OBJECT) return null

        val nullable = param.resolvedType.isMarkedNullable

        return SupplierData(
            initializer = initializer(nullable, param.name!!.asString(), declaration.toClassName()),
            implType = (if (nullable) nullableSupplierImplType else supplierImplType)
                .parameterizedBy(param.resolvedType.makeNotNullable().toTypeName(), kiraScope)
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
            addStatement("value = %T,", objectName)
            addStatement("typeName = %S,", objectName.canonicalName)
            if (nullable) addStatement("nullByDefault = true")
        }
        addStatement(")")
    }
}
