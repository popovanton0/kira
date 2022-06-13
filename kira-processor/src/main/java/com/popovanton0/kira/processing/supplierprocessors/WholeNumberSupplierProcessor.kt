package com.popovanton0.kira.processing.supplierprocessors

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.U_BYTE
import com.squareup.kotlinpoet.U_INT
import com.squareup.kotlinpoet.U_LONG
import com.squareup.kotlinpoet.U_SHORT

object WholeNumberSupplierProcessor : SupplierProcessor {
    private val supplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "WholeNumberSupplierBuilder")
    private val nullableSupplierImplType =
        ClassName(SUPPLIERS_PKG_NAME, "NullableWholeNumberSupplierBuilder")

    private data class Constants(
        val typeName: String,
        val supplierMethodName: MemberName,
        val nullableSupplierMethodName: MemberName,
        val supplierImplType: TypeName,
        val nullableSupplierImplType: TypeName,
    )

    private val constants = buildList(8) {
        Constants(
            typeName = BYTE.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "byte"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableByte"),
            supplierImplType = supplierImplType.parameterizedBy(BYTE),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(BYTE)
        ).also(::add)
        Constants(
            typeName = SHORT.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "short"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableShort"),
            supplierImplType = supplierImplType.parameterizedBy(SHORT),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(SHORT)
        ).also(::add)
        Constants(
            typeName = INT.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "int"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableInt"),
            supplierImplType = supplierImplType.parameterizedBy(INT),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(INT)
        ).also(::add)
        Constants(
            typeName = LONG.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "long"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableLong"),
            supplierImplType = supplierImplType.parameterizedBy(LONG),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(LONG)
        ).also(::add)
        Constants(
            typeName = U_BYTE.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "uByte"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableUByte"),
            supplierImplType = supplierImplType.parameterizedBy(U_BYTE),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(U_BYTE)
        ).also(::add)
        Constants(
            typeName = U_SHORT.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "uShort"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableUShort"),
            supplierImplType = supplierImplType.parameterizedBy(U_SHORT),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(U_SHORT)
        ).also(::add)
        Constants(
            typeName = U_INT.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "uInt"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableUInt"),
            supplierImplType = supplierImplType.parameterizedBy(U_INT),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(U_INT)
        ).also(::add)
        Constants(
            typeName = U_LONG.canonicalName,
            supplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "uLong"),
            nullableSupplierMethodName = MemberName(SUPPLIERS_PKG_NAME, "nullableULong"),
            supplierImplType = supplierImplType.parameterizedBy(U_LONG),
            nullableSupplierImplType = nullableSupplierImplType.parameterizedBy(U_LONG)
        ).also(::add)
    }

    /**
     * ```
     * int = int(paramName = "int")
     * ```
     */
    override fun provideSupplierFor(param: FunctionParameter): SupplierData? {
        val declaration = param.resolvedType.declaration
        val paramTypeName = declaration.qualifiedName?.asString() ?: return null
        val constants = constants.find { it.typeName == paramTypeName } ?: return null
        val nullable = param.resolvedType.isMarkedNullable

        return SupplierData(
            initializer = initializer(nullable, param.name!!.asString(), constants),
            implType = if (nullable) constants.nullableSupplierImplType
            else constants.supplierImplType
        )
    }

    private fun initializer(nullable: Boolean, paramName: String, constants: Constants): CodeBlock {
        val funName = if (nullable) constants.nullableSupplierMethodName
        else constants.supplierMethodName
        return CodeBlock.of("%M(paramName = %S)", funName, paramName)
    }
}
