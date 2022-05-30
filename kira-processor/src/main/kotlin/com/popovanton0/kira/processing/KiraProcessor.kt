@file:OptIn(KotlinPoetKspPreview::class)

package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class KiraProcessor(
    private val environment: SymbolProcessorEnvironment,
    private val supplierProcessors: List<SupplierProcessor>,
) : SymbolProcessor {

    internal companion object {
        internal const val KIRA_ROOT_PKG_NAME = "com.popovanton0.kira"
        internal val kiraAnnClass = Kira::class.java
        internal val kiraRootAnnClass = KiraRoot::class.java

        private val kotlinSimpleFunNameRegex = "[a-zA-Z_][a-zA-Z_\\d]*".toRegex()
        private val supplierInterfaceName = ClassName("$SUPPLIERS_PKG_NAME.base", "Supplier")
        private val kiraScopeName = ClassName("$SUPPLIERS_PKG_NAME.compound", "KiraScope")
        private val generatedKiraScopeName =
            ClassName("$SUPPLIERS_PKG_NAME.compound", "GeneratedKiraScopeWithImpls")
        private val abstractImplsScopeName =
            generatedKiraScopeName.nestedClass("SupplierImplsScope")
        private val abstractImplsScope_implsChanged =
            generatedKiraScopeName.member("implChanged")
        private val collectSuppliersReturnType =
            LIST.parameterizedBy(supplierInterfaceName.parameterizedBy(STAR))
        private val kiraProviderName = ClassName(SUPPLIERS_PKG_NAME, "KiraProvider")
        private val kiraSupplierName = ClassName(SUPPLIERS_PKG_NAME, "Kira")
        private val kiraBuilderFunName = MemberName(SUPPLIERS_PKG_NAME, "kira")
        private val kiraMissesName = ClassName(SUPPLIERS_PKG_NAME, "KiraMisses")
        private val injectorClassName = ClassName("$SUPPLIERS_PKG_NAME.compound", "Injector")
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val rootAnns = resolver.getSymbolsWithAnnotation(kiraRootAnnClass.canonicalName).toList()

        if (rootAnns.isEmpty()) return emptyList()
        if (rootAnns.size > 1) Errors.manyRootClasses(rootAnns)
        val kiraRootAnn = rootAnns.first().getAnnotationsByType(KiraRoot::class).first()


        resolver.getSymbolsWithAnnotation(kiraAnnClass.canonicalName).forEach { function ->
            isValidKiraFunction(function)
            val funPkgName = function.packageName.asString()
            val funSimpleName = function.simpleName.asString()
            val fullFunName = function.qualifiedName!!.asString()
            val kiraAnn = function.getAnnotationsByType(Kira::class).first()
            val fileName = kiraAnn.name.ifEmpty {
                require(funSimpleName.matches(kotlinSimpleFunNameRegex)) {
                    // todo write a lint rule for that
                    Errors.funWithUnicodeCharsInTheName(fullFunName)
                }
                funSimpleName
            }

            val injectorGeneration = InjectorGeneration(function)

            val parameterSuppliers: List<ParameterSupplier> = function.parameters.map { param ->
                param.name ?: Errors.funParamWithNoName(fullFunName)
                val functionParameter = FunctionParameter(param)
                val supplierData = supplierProcessors.firstNotNullOfOrNull {
                    it.provideSupplierFor(functionParameter)
                }

                if (supplierData != null)
                    ParameterSupplier.Provided(functionParameter, supplierData)
                else
                    ParameterSupplier.Empty(functionParameter)
            }

            val generatedPkgName = "${Kira.GENERATED_PACKAGE_PREFIX}.$funPkgName"
            val scopeName = ClassName(generatedPkgName, "${fileName}Scope")
            val supplierImplsScopeName = scopeName.nestedClass("SupplierImplsScope")

            val file = FileSpec.builder(
                packageName = generatedPkgName,
                fileName = fileName
            )
                .addFileComment("This file is autogenerated. Do not edit it")
                .addType(
                    kiraProvider(
                        injectorGeneration,
                        generatedPkgName,
                        fileName,
                        scopeName,
                        funPkgName,
                        funSimpleName,
                        parameterSuppliers
                    )
                )
                .addType(scopeClass(scopeName, supplierImplsScopeName, parameterSuppliers))
                .build()

            file.writeTo(environment.codeGenerator, aggregating = false)
        }

        return emptyList()
    }

    private fun scopeClass(
        scopeName: ClassName,
        supplierImplsScopeName: ClassName,
        parameterSuppliers: List<ParameterSupplier>
    ) = TypeSpec.classBuilder(scopeName)
        .superclass(generatedKiraScopeName.parameterizedBy(supplierImplsScopeName))
        .addProperty(supplierImplsScopeProp(supplierImplsScopeName))
        .addType(
            supplierImplsClass(
                supplierImplsScopeName,
                scopeName,
                parameterSuppliers.filterIsInstance<ParameterSupplier.Provided>()
            )
        )
        .addProperties(lateinitSupplierProps(parameterSuppliers))
        .addFunction(collectSuppliersFun(parameterSuppliers))
        .build()

    private fun kiraProvider(
        injectorGeneration: InjectorGeneration,
        generatedPkgName: String,
        fileName: String,
        scopeName: ClassName,
        funPkgName: String,
        funSimpleName: String,
        parameterSuppliers: List<ParameterSupplier>
    ): TypeSpec {
        val kiraProviderName = ClassName(generatedPkgName, "Kira_$fileName")
        val kiraProvider = TypeSpec.classBuilder(kiraProviderName)
        val primaryConstructor = FunSpec.constructorBuilder()

        if (parameterSuppliers.any { it is ParameterSupplier.Empty }) {
            kiraProvider.addType(missesClass(parameterSuppliers))
            val missesClassType = kiraProviderName.nestedClass("Misses")
            val missesProviderType = LambdaTypeName
                .get(receiver = kiraScopeName, returnType = missesClassType)
            val missesPropertyName = "misses"
            primaryConstructor.addParameter(missesPropertyName, missesProviderType)

            kiraProvider.addProperty(
                PropertySpec.builder(missesPropertyName, missesClassType)
                    .initializer("%T().%L()", kiraScopeName, missesPropertyName)
                    .build()
            )
        }
        val injectorPropertyName = "injector"
        if (injectorGeneration.skip) {
            val injectorProviderType = LambdaTypeName.get(
                receiver = scopeName,
                returnType = injectorClassName.parameterizedBy(UNIT)
            )
            primaryConstructor.addParameter(injectorPropertyName, injectorProviderType)
            kiraProvider.addKdoc("@param injector wasn't generated because:\n")
            kiraProvider.addKdoc(injectorGeneration.noInjectorReasonMsg)
            kiraProvider.addProperty(
                PropertySpec.builder(injectorPropertyName, injectorProviderType)
                    .initializer(injectorPropertyName)
                    .build()
            )
        }
        kiraProvider
            .primaryConstructor(primaryConstructor.build())
            .addSuperinterface(Companion.kiraProviderName.parameterizedBy(scopeName))
            .addProperty(
                PropertySpec.builder(
                    "kira", kiraSupplierName.parameterizedBy(scopeName), KModifier.OVERRIDE
                ).initializer(
                    kiraFun(
                        scopeName,
                        parameterSuppliers,
                        kiraProviderName,
                        injectorGeneration,
                        injectorPropertyName,
                        funPkgName,
                        funSimpleName
                    )
                ).build()
            ).build()

        return kiraProvider.build()
    }

    private fun kiraFun(
        scopeName: ClassName,
        parameterSuppliers: List<ParameterSupplier>,
        kiraProviderName: ClassName,
        injectorGeneration: InjectorGeneration,
        injectorPropertyName: String,
        funPkgName: String,
        funSimpleName: String
    ) = buildCodeBlock {
        beginControlFlow("%M(%T()) {", kiraBuilderFunName, scopeName)
        parameterSuppliers
            .forEach { parameterSupplier ->
                val paramName = parameterSupplier.parameter.name!!.asString()
                if (parameterSupplier is ParameterSupplier.Provided) add(
                    "%N = %L\n",
                    paramName, parameterSupplier.supplierData.initializer
                ) else add(
                    "%N = this@%T.misses.%N\n",
                    paramName, kiraProviderName, paramName,
                )
            }
        if (injectorGeneration.skip) {
            addStatement("%N()", kiraProviderName.member(injectorPropertyName))
        } else {
            beginControlFlow("injector {")
            add(
                injectorGeneration.injectorFunctionCall(
                    kiraProviderName, funPkgName, funSimpleName, parameterSuppliers
                )
            )
            endControlFlow()
        }
        endControlFlow()
    }

    private fun missesClass(parameterSuppliers: List<ParameterSupplier>): TypeSpec {
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
                        val paramTypeName = supplierInterfaceName
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

    private fun supplierImplsClass(
        implsScopeClassName: ClassName,
        scopeClassName: ClassName,
        parameterSuppliers: List<ParameterSupplier.Provided>,
    ) = TypeSpec.classBuilder(implsScopeClassName)
        .superclass(abstractImplsScopeName)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("scope", scopeClassName)
                .build()
        )
        .addProperty(
            PropertySpec.builder("scope", scopeClassName)
                .initializer("scope")
                .addModifiers(KModifier.PRIVATE)
                .build()
        )
        .apply {
            parameterSuppliers.forEach { parameterSupplier ->
                supplierImplProperty(parameterSupplier, scopeClassName)
            }
        }
        .build()

    private fun TypeSpec.Builder.supplierImplProperty(
        parameterSupplier: ParameterSupplier.Provided,
        scopeClassName: ClassName
    ) {
        val propName = parameterSupplier.parameter.name!!.asString()
        val supplierImpl = parameterSupplier.supplierData.implType
        val lateinitProp = scopeClassName.member(propName)
        addProperty(
            PropertySpec.builder(propName, supplierImpl)
                .mutable()
                .getter(supplierImplPropertyGetter(lateinitProp, supplierImpl))
                .setter(supplierImplPropertySetter(supplierImpl, lateinitProp))
                .build()
        )
    }

    /** `set(value) { scope.text = value }` */
    private fun supplierImplPropertySetter(
        supplierImpl: TypeName,
        lateinitProp: MemberName
    ) = FunSpec.setterBuilder()
        .addParameter("value", supplierImpl)
        .addStatement("scope.%N = value", lateinitProp)
        .build()

    /** `scope.text as? StringSupplierBuilder ?: implChanged()` */
    private fun supplierImplPropertyGetter(
        lateinitProp: MemberName,
        supplierImpl: TypeName
    ) = FunSpec.getterBuilder().addStatement(
        "return scope.%N as? %T ?: %N()",
        lateinitProp,
        supplierImpl,
        abstractImplsScope_implsChanged
    ).build()

    private fun lateinitSupplierProps(
        parameterSuppliers: List<ParameterSupplier>
    ): List<PropertySpec> = parameterSuppliers.map { parameterSupplier ->
        val propName = parameterSupplier.parameter.name!!.asString()
        val supplierTypeArg = parameterSupplier.parameter.resolvedType.toTypeName()
        PropertySpec
            .builder(
                name = propName,
                type = supplierInterfaceName.parameterizedBy(supplierTypeArg),
                KModifier.LATEINIT
            )
            .mutable()
            .build()
    }

    /** `override val supplierImplsScope: SupplierImplsScope = SupplierImplsScope(this)` */
    private fun supplierImplsScopeProp(implsScopeClassName: ClassName) = PropertySpec.builder(
        name = "supplierImplsScope",
        type = implsScopeClassName,
        KModifier.OVERRIDE
    ).initializer("%T(this)", implsScopeClassName).build()

    private val listOf = MemberName("kotlin.collections", "listOf")

    /**
     * `override fun collectSuppliers(): List<Supplier<*>> = listOf(text, isRed, skill, food, car,
     *     carN, rock)`
     */
    private fun collectSuppliersFun(parameterSuppliers: List<ParameterSupplier>) =
        FunSpec.builder("collectSuppliers")
            .addModifiers(KModifier.OVERRIDE)
            .returns(collectSuppliersReturnType)
            .addCode(
                CodeBlock
                    .builder()
                    .add("return %M(", listOf)
                    .apply {
                        parameterSuppliers.forEach { parameterSupplier ->
                            val propName = parameterSupplier.parameter.name!!.asString()
                            add(propName)
                            add(", ")
                        }
                    }
                    .add(")")
                    .build()
            )
            .build()

    @OptIn(ExperimentalContracts::class)
    private fun isValidKiraFunction(function: KSAnnotated) {
        contract {
            returns() implies (function is KSFunctionDeclaration)
        }
        require(function is KSFunctionDeclaration) {
            "Only functions can be annotated with @${kiraAnnClass.simpleName}, but " +
                    "${function.location} is not a function"
        }
    }
}
