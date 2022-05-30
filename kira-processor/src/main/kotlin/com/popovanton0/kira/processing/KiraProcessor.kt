@file:OptIn(KotlinPoetKspPreview::class)

package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot
import com.popovanton0.kira.processing.generators.InjectorGenerator
import com.popovanton0.kira.processing.generators.MissesClassGenerator
import com.popovanton0.kira.processing.generators.ScopeClassGenerator
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
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
        internal val supplierInterfaceName = ClassName("$SUPPLIERS_PKG_NAME.base", "Supplier")
        private val kiraScopeName = ClassName("$SUPPLIERS_PKG_NAME.compound", "KiraScope")
        internal val generatedKiraScopeName =
            ClassName("$SUPPLIERS_PKG_NAME.compound", "GeneratedKiraScopeWithImpls")
        private val kiraProviderName = ClassName(SUPPLIERS_PKG_NAME, "KiraProvider")
        private val kiraSupplierName = ClassName(SUPPLIERS_PKG_NAME, "Kira")
        private val kiraBuilderFunName = MemberName(SUPPLIERS_PKG_NAME, "kira")
        private val injectorClassName = ClassName("$SUPPLIERS_PKG_NAME.compound", "Injector")
        private val injectorFunName = MemberName("$SUPPLIERS_PKG_NAME.compound", "injector")
    }

    @KspExperimental
    private fun getKiraRootAnn(resolver: Resolver): KiraRoot? {
        var rootAnns = resolver.getSymbolsWithAnnotation(kiraRootAnnClass.canonicalName)
        if (rootAnns.none()) return null
        if (rootAnns.take(2).count() > 1) rootAnns = rootAnns
            .filter { it.origin == Origin.KOTLIN || it.origin == Origin.JAVA }
        if (rootAnns.take(2).count() > 1) Errors.manyRootClasses(rootAnns.toList())
        return rootAnns.single().getAnnotationsByType(KiraRoot::class).single()
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val kiraRootAnn = getKiraRootAnn(resolver) ?: return emptyList()

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

            val injectorGenerator = InjectorGenerator(function)

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
                        injectorGenerator,
                        generatedPkgName,
                        fileName,
                        scopeName,
                        funPkgName,
                        funSimpleName,
                        parameterSuppliers
                    )
                )
                .addType(
                    ScopeClassGenerator.generate(
                        fullFunName,
                        scopeName,
                        supplierImplsScopeName,
                        parameterSuppliers
                    )
                )
                .build()

            file.writeTo(environment.codeGenerator, aggregating = false)
        }

        return emptyList()
    }

    private fun kiraProvider(
        injectorGenerator: InjectorGenerator,
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
            kiraProvider.addType(MissesClassGenerator.generate(parameterSuppliers))
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
        if (injectorGenerator.skip) {
            val injectorProviderType = LambdaTypeName.get(
                receiver = scopeName,
                returnType = injectorClassName.parameterizedBy(UNIT)
            )
            primaryConstructor.addParameter(injectorPropertyName, injectorProviderType)
            kiraProvider.addKdoc("@param injector wasn't generated because:\n")
            kiraProvider.addKdoc(injectorGenerator.noInjectorReasonMsg)
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
                        injectorGenerator,
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
        injectorGenerator: InjectorGenerator,
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
        if (injectorGenerator.skip) {
            addStatement("%N()", kiraProviderName.member(injectorPropertyName))
        } else {
            beginControlFlow("%M {", injectorFunName)
            add(
                injectorGenerator.injectorFunctionCall(
                    kiraProviderName, funPkgName, funSimpleName, parameterSuppliers
                )
            )
            endControlFlow()
        }
        endControlFlow()
    }

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
