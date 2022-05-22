package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName

object ObjectSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "CompoundSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "NullableCompoundSupplierBuilder")
    private val kiraScope =
        ClassName("$SUPPLIERS_PKG_NAME.compound", "KiraScope")

    /**
     * ```
     * engine = nullableSingleValue("engine", Engine("single value"), nullByDefault = true)
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration as? KSClassDeclaration ?: return null
        if (declaration.classKind != ClassKind.OBJECT) return null

        val nullable = param.resolvedType.isMarkedNullable

        return SupplierData(
            supplierInitializer = CodeBlock.of("TODO()"),
            supplierImplType = (if (nullable) nullableSupplierImplType else supplierImplType)
                .parameterizedBy(param.resolvedType.makeNotNullable().toTypeName(), kiraScope)
        )
    }
}
