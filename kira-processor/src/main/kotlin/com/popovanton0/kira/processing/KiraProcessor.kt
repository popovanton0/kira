@file:OptIn(KotlinPoetKspPreview::class)

package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot
import com.popovanton0.kira.processing.supplierprocessors.BooleanSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.EnumSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.ObjectSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.StringSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierData
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class KiraProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val kotlinSimpleFunNameRegex = "[a-zA-Z_][a-zA-Z_\\d]*".toRegex()
    private val supplierInterfaceName = ClassName("$SUPPLIERS_PKG_NAME.base", "Supplier")
    private val generatedKiraScopeName =
        ClassName("$KIRA_ROOT_PKG_NAME.suppliers.compound", "GeneratedKiraScopeWithImpls")
    private val abstractImplsScopeName =
        generatedKiraScopeName.nestedClass("SupplierImplsScope")
    private val abstractImplsScope_implsChanged =
        generatedKiraScopeName.member("implChanged")
    private val collectSuppliersReturnType =
        LIST.parameterizedBy(supplierInterfaceName.parameterizedBy(STAR))
    private val supplierProcessors = listOf(
        StringSupplierProcessor,
        BooleanSupplierProcessor,
        EnumSupplierProcessor,
        ObjectSupplierProcessor,
    )

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val rootAnns = resolver.getSymbolsWithAnnotation(kiraRootAnnClass.canonicalName).toList()
        if (false) {
            if (rootAnns.isEmpty()) return emptyList()
            if (rootAnns.size > 1) Errors.manyRootClasses(rootAnns)
            val kiraRootAnn = rootAnns.first().getAnnotationsByType(KiraRoot::class).first()
        }

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

            val supplierDataList: List<SupplierData> = function.parameters.mapNotNull { param ->
                param.name ?: Errors.funParamWithNoName(fullFunName)
                val functionParameter = FunctionParameter(param)
                supplierProcessors.firstNotNullOfOrNull { it.provideSupplierFor(functionParameter) }
            }

            val generatedPkgName = "$KIRA_ROOT_PKG_NAME.generated.$funPkgName"
            val scopeName = ClassName(generatedPkgName, "${fileName}Scope")
            val supplierImplsScopeName = scopeName.nestedClass("SupplierImplsScope")

            val file = FileSpec.builder(
                packageName = generatedPkgName,
                fileName = fileName
            ).addType(
                TypeSpec.classBuilder(scopeName)
                    .superclass(generatedKiraScopeName.parameterizedBy(supplierImplsScopeName))
                    .addProperty(supplierImplsScopeProp(supplierImplsScopeName))
                    .addType(
                        supplierImplsClass(supplierImplsScopeName, scopeName, supplierDataList)
                    )
                    .addProperties(lateinitSupplierProps(supplierDataList))
                    .addFunction(collectSuppliersFun(supplierDataList))
                    .build()
            ).build()

            file.writeTo(environment.codeGenerator, aggregating = false)
        }

        return emptyList()
    }

    private fun supplierImplsClass(
        implsScopeClassName: ClassName,
        scopeClassName: ClassName,
        supplierDataList: List<SupplierData>
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
            supplierDataList.forEach { supplierData ->
                supplierImplProperty(supplierData, scopeClassName)
            }
        }
        .build()

    private fun TypeSpec.Builder.supplierImplProperty(
        supplierData: SupplierData,
        scopeClassName: ClassName
    ) {
        val propName = supplierData.functionParameter.parameter.name!!.asString()
        val supplierImpl = supplierData.supplierImplType
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

    private fun lateinitSupplierProps(supplierDataList: List<SupplierData>): List<PropertySpec> =
        supplierDataList.map { supplierData ->
            val propName = supplierData.functionParameter.parameter.name!!.asString()
            val supplierTypeArg = supplierData.functionParameter.resolvedType.toTypeName()
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
    private fun collectSuppliersFun(supplierDataList: List<SupplierData>) =
        FunSpec.builder("collectSuppliers")
            .addModifiers(KModifier.OVERRIDE)
            .returns(collectSuppliersReturnType)
            .addCode(
                CodeBlock
                    .builder()
                    .add("return %M(", listOf)
                    .apply {
                        supplierDataList.forEach { supplierData ->
                            val propName =
                                supplierData.functionParameter.parameter.name!!.asString()
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
        val fullFunName = function.qualifiedName?.asString()
        require(function.functionKind == FunctionKind.TOP_LEVEL) {
            "Only top level functions are supported, but $fullFunName is not"
        }
        require(!function.isPrivate()) {
            "Function $fullFunName is private, thus it cannot be called"
        }
        require(function.typeParameters.isEmpty()) {
            "Functions with generics are not supported: $fullFunName"
        }
        require(Modifier.SUSPEND !in function.modifiers) {
            "Suspend functions are not supported: $fullFunName"
        }
    }
}

internal const val KIRA_ROOT_PKG_NAME = "com.popovanton0.kira"
internal val kiraAnnClass = Kira::class.java
internal val kiraRootAnnClass = Kira::class.java
