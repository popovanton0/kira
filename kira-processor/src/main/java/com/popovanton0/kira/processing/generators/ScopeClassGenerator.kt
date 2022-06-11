package com.popovanton0.kira.processing.generators

import com.popovanton0.kira.processing.Errors
import com.popovanton0.kira.processing.KiraProcessor.Companion.generatedKiraScopeName
import com.popovanton0.kira.processing.KiraProcessor.Companion.supplierBuilderInterfaceName
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName

internal object ScopeClassGenerator {
    private val collectSuppliersReturnType =
        LIST.parameterizedBy(supplierBuilderInterfaceName.parameterizedBy(STAR))
    private const val supplierImplsScopePropName = "$$\$supplierImplsScope$$$"

    internal fun generate(
        fullFunName: String,
        scopeName: ClassName,
        supplierImplsScopeName: ClassName,
        parameterSuppliers: List<ParameterSupplier>
    ) = TypeSpec.classBuilder(scopeName)
        .superclass(generatedKiraScopeName.parameterizedBy(supplierImplsScopeName))
        .addProperty(supplierImplsScopeProp(supplierImplsScopeName))
        .addType(
            SupplierImplsClassGenerator.generate(
                implsScopeClassName = supplierImplsScopeName,
                scopeClassName = scopeName,
                parameterSuppliers =
                parameterSuppliers.filterIsInstance<ParameterSupplier.Provided>()
            )
        )
        .addProperties(lateinitSupplierProps(parameterSuppliers.checkNoReservedNames(fullFunName)))
        .addFunction(collectSuppliersFun(parameterSuppliers))
        .build()

    private fun List<ParameterSupplier>.checkNoReservedNames(
        fullFunName: String
    ): List<ParameterSupplier> = onEach {
        val paramName = it.parameter.name!!.asString()
        require(paramName != supplierImplsScopePropName) {
            Errors.reservedParamName(fullFunName, paramName)
        }
    }

    private fun lateinitSupplierProps(
        parameterSuppliers: List<ParameterSupplier>
    ): List<PropertySpec> = parameterSuppliers.map { parameterSupplier ->
        val propName = parameterSupplier.parameter.name!!.asString()
        val supplierTypeArg = parameterSupplier.parameter.resolvedType.toTypeName()
        PropertySpec
            .builder(
                name = propName,
                type = supplierBuilderInterfaceName.parameterizedBy(supplierTypeArg),
                KModifier.LATEINIT
            )
            .mutable()
            .build()
    }

    /** `override val $$$supplierImplsScope$$$: SupplierImplsScope = SupplierImplsScope(this)` */
    private fun supplierImplsScopeProp(implsScopeClassName: ClassName) = PropertySpec.builder(
        name = supplierImplsScopePropName,
        type = implsScopeClassName,
        KModifier.OVERRIDE, KModifier.PROTECTED
    ).initializer("%T(this)", implsScopeClassName).build()

    private val listOf = MemberName("kotlin.collections", "listOf")

    /**
     * `override fun collectSuppliers(): List<Supplier<*>> = listOf(text, isRed, skill, food, car,
     *     carN, rock)`
     */
    private fun collectSuppliersFun(parameterSuppliers: List<ParameterSupplier>) =
        FunSpec.builder("collectSupplierBuilders")
            .addModifiers(KModifier.OVERRIDE)
            .returns(collectSuppliersReturnType)
            .addCode(
                CodeBlock.builder()
                    .add("return %M(", listOf)
                    .apply {
                        parameterSuppliers.forEach { parameterSupplier ->
                            val propName = parameterSupplier.parameter.name!!.asString()
                            add("%N, ", propName)
                        }
                    }
                    .add(")")
                    .build()
            )
            .build()
}