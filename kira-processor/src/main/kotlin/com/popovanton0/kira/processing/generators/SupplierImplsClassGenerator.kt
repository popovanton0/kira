package com.popovanton0.kira.processing.generators

import com.popovanton0.kira.processing.KiraProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member

internal object SupplierImplsClassGenerator {
    private val abstractImplsScopeName =
        KiraProcessor.generatedKiraScopeName.nestedClass("SupplierImplsScope")
    private val abstractImplsScope_implsChanged =
        KiraProcessor.generatedKiraScopeName.member("implChanged")

    internal fun generate(
        implsScopeClassName: ClassName,
        scopeClassName: ClassName,
        parameterSuppliers: List<ParameterSupplier.Provided>,
    ): TypeSpec {
        val scopePropertyName = createScopePropertyName(parameterSuppliers)
        return TypeSpec.classBuilder(implsScopeClassName)
            .superclass(abstractImplsScopeName)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(scopePropertyName, scopeClassName)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(scopePropertyName, scopeClassName)
                    .initializer(scopePropertyName)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .apply {
                parameterSuppliers.forEach { parameterSupplier ->
                    addProperty(
                        supplierImplProperty(parameterSupplier, scopeClassName, scopePropertyName)
                    )
                }
            }
            .build()
    }

    /**
     * Creates a name that does not collide with other param's names, a unique name
     */
    private fun createScopePropertyName(
        parameterSuppliers: List<ParameterSupplier.Provided>
    ): String {
        var scopePropertyName = "scope"
        while (parameterSuppliers.any { it.parameter.name!!.asString() == scopePropertyName }) {
            scopePropertyName += "$"
        }
        return scopePropertyName
    }

    private fun supplierImplProperty(
        parameterSupplier: ParameterSupplier.Provided,
        scopeClassName: ClassName,
        scopePropertyName: String,
    ): PropertySpec {
        val propName = parameterSupplier.parameter.name!!.asString()
        val supplierImpl = parameterSupplier.supplierData.implType
        val lateinitProp = scopeClassName.member(propName)

        return PropertySpec.builder(propName, supplierImpl)
            .mutable()
            .getter(supplierImplPropertyGetter(lateinitProp, supplierImpl, scopePropertyName))
            .setter(supplierImplPropertySetter(supplierImpl, lateinitProp, scopePropertyName))
            .build()
    }

    /** `set(value) { scope.text = value }` */
    private fun supplierImplPropertySetter(
        supplierImpl: TypeName,
        lateinitProp: MemberName,
        scopePropertyName: String
    ) = FunSpec.setterBuilder()
        .addParameter("value", supplierImpl)
        .addStatement("%N.%N = value", scopePropertyName, lateinitProp)
        .build()

    /** `scope.text as? StringSupplierBuilder ?: implChanged()` */
    private fun supplierImplPropertyGetter(
        lateinitProp: MemberName,
        supplierImpl: TypeName,
        scopePropertyName: String
    ) = FunSpec.getterBuilder().addStatement(
        "return %N.%N as? %T ?: %N()",
        scopePropertyName,
        lateinitProp,
        supplierImpl,
        abstractImplsScope_implsChanged
    ).build()
}