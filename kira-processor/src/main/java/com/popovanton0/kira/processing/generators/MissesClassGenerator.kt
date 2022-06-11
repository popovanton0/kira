package com.popovanton0.kira.processing.generators

import com.popovanton0.kira.processing.KiraProcessor.Companion.supplierBuilderInterfaceName
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName

internal object MissesClassGenerator {
    private val kiraMissesName = ClassName(SUPPLIERS_PKG_NAME, "KiraMisses")

    internal fun generate(parameterSuppliers: List<ParameterSupplier>): TypeSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
        return TypeSpec.classBuilder("Misses")
            .addSuperinterface(kiraMissesName)
            .addModifiers(KModifier.DATA)
            .apply {
                parameterSuppliers
                    .filterIsInstance<ParameterSupplier.Empty>()
                    .forEach { parameterSupplier ->
                        val param = parameterSupplier.parameter
                        val paramName = param.name!!.asString()
                        val paramTypeName = supplierBuilderInterfaceName
                            .parameterizedBy(param.resolvedType.toTypeName())

                        constructorBuilder.addParameter(paramName, paramTypeName)
                        addProperty(
                            PropertySpec.builder(paramName, paramTypeName)
                                .initializer(paramName)
                                .build()
                        )
                    }
            }
            .primaryConstructor(constructorBuilder.build())
            .build()
    }
}