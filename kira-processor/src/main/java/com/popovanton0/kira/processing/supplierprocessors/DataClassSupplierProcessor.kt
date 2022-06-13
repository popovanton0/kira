package com.popovanton0.kira.processing.supplierprocessors

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName

object DataClassSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.dataclass", "DataClassSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName("$SUPPLIERS_PKG_NAME.dataclass", "NullableDataClassSupplierBuilder")
    private val builderFunName =
        MemberName("$SUPPLIERS_PKG_NAME.dataclass", "dataClass")
    private val nullableBuilderFunName =
        MemberName("$SUPPLIERS_PKG_NAME.dataclass", "nullableDataClass")

    /**
     * ```
     * car = dataClass(paramName = "car", dataClass = Car::class)
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration as? KSClassDeclaration ?: return null
        if (Modifier.DATA !in declaration.modifiers) return null
        val nullable = param.resolvedType.isMarkedNullable
        val className = declaration.toClassName()

        return SupplierData(
            initializer = initializer(nullable, param.name!!.asString(), className),
            implType = (if (nullable) nullableSupplierImplType else supplierImplType)
                .parameterizedBy(className)
        )
    }

    private fun initializer(
        nullable: Boolean,
        paramName: String,
        className: ClassName,
    ): CodeBlock {
        val funName = if (nullable) nullableBuilderFunName else builderFunName
        return CodeBlock.of(
            "%M(paramName = %S, dataClass = %T::class)", funName, paramName, className,
        )
    }
}
