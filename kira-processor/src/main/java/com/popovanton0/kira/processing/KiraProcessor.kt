@file:OptIn(KotlinPoetKspPreview::class)

package com.popovanton0.kira.processing

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Origin
import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.annotations.KiraRoot
import com.popovanton0.kira.processing.generators.DeclarationsAggregator
import com.popovanton0.kira.processing.generators.InjectorGenerator
import com.popovanton0.kira.processing.generators.MissesClassGenerator
import com.popovanton0.kira.processing.generators.RegistryGenerator
import com.popovanton0.kira.processing.generators.ScopeClassGenerator
import com.popovanton0.kira.processing.supplierprocessors.base.ParameterSupplier
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor.Companion.SUPPLIERS_PKG_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo

class KiraProcessor(
    private val environment: SymbolProcessorEnvironment,
    private val supplierProcessors: List<SupplierProcessor>,
) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator

    internal companion object {
        internal const val KIRA_ROOT_PKG_NAME = "com.popovanton0.kira"
        internal val kiraAnnClass = Kira::class.java
        internal val kiraRootAnnClass = KiraRoot::class.java

        internal val kiraProviderName = ClassName(SUPPLIERS_PKG_NAME, "KiraProvider")
        internal val supplierInterfaceName = ClassName("$SUPPLIERS_PKG_NAME.base", "Supplier")
        internal val generatedKiraScopeName =
            ClassName("$SUPPLIERS_PKG_NAME.compound", "GeneratedKiraScopeWithImpls")

        private val kiraScopeName = ClassName("$SUPPLIERS_PKG_NAME.compound", "KiraScope")
        private val kiraSupplierName = ClassName(SUPPLIERS_PKG_NAME, "Kira")
        private val kiraBuilderFunName = MemberName(SUPPLIERS_PKG_NAME, "kira")
        private val injectorClassName = ClassName("$SUPPLIERS_PKG_NAME.compound", "Injector")
        private val injectorFunName = MemberName("$SUPPLIERS_PKG_NAME.compound", "injector")
    }

    @KspExperimental
    private fun getKiraRootClass(resolver: Resolver): KSAnnotated? {
        val rootAnns = resolver.getSymbolsWithAnnotation(kiraRootAnnClass.canonicalName)
            .filter { it.origin == Origin.KOTLIN || it.origin == Origin.JAVA }
        if (rootAnns.none()) return null
        return rootAnns.singleOrNull() ?: Errors.manyRootClasses(rootAnns.toList())
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val kiraRootClass = getKiraRootClass(resolver)
        val kiraRootAnn = kiraRootClass?.getAnnotationsByType(KiraRoot::class)?.single()

        val kiraDeclarations = DeclarationsAggregator.aggregate(resolver)
        if (kiraRootAnn == null) {
            if (kiraDeclarations.names.isNotEmpty()) {
                DeclarationsAggregator
                    .aggregatorFile(resolver, kiraDeclarations.names)
                    .writeTo(codeGenerator, aggregating = true, kiraDeclarations.originatingKSFiles)
            }
            return emptyList()
        }

        val generateRegistry = kiraRootAnn.generateRegistry
        val registryRecords = mutableListOf<RegistryGenerator.RegistryRecord>()

        val functionsInAllModules: List<KiraFunction> = resolver
            .getDeclarationsFromPackage(DeclarationsAggregator.PACKAGE_PREFIX)
            .map(DeclarationsAggregator::extractNamesFromAggregator)
            // this is the root module, and [kiraDeclarations] are not written into [aggregatorFile]
            // thus, they need to be added separately
            .plus(element = kiraDeclarations.names)
            .flatten() // function names from each module -> names across all modules
            .toList()
            .distinct()
            .flatMap { qualifiedName -> // function names across all modules
                resolver
                    .getFunctionDeclarationsByName(qualifiedName, includeTopLevel = true)
                    .toList()
            }
            .map { KiraFunction(it, it.kiraAnn()) }
            .requireUniqueFunctionNames()

        functionsInAllModules.forEach { kiraFunction ->
            val function = kiraFunction.function

            val funPkgName = function.packageName.asString()
            val fullFunName = function.qualifiedName!!.asString()
            val correctedFunSimpleName = correctedQualifiedName(kiraFunction)
                .substringAfterLast('.')

            val injectorGenerator = InjectorGenerator(function)
            val paramSuppliers: List<ParameterSupplier> = findSuppliersForFunParams(function)

            val generatedPkgName = "${Kira.GENERATED_PACKAGE_PREFIX}.$funPkgName"
            val kiraProviderName = ClassName(generatedPkgName, "Kira_$correctedFunSimpleName")
            val scopeName = ClassName(generatedPkgName, "${correctedFunSimpleName}Scope")
            val supplierImplsScopeName = scopeName.nestedClass("SupplierImplsScope")

            FileSpec.builder(
                packageName = generatedPkgName,
                fileName = "${sha256(correctedFunSimpleName).take(4)}_${correctedFunSimpleName}"
            )
                .addFileComment("This file is autogenerated. Do not edit it")
                .addType(
                    kiraProvider(
                        injectorGenerator, scopeName, paramSuppliers, kiraProviderName,
                        funName = MemberName(funPkgName, function.simpleName.asString())
                    )
                )
                .addType(
                    ScopeClassGenerator
                        .generate(fullFunName, scopeName, supplierImplsScopeName, paramSuppliers)
                )
                .build()
                .writeTo(
                    codeGenerator, aggregating = false,
                    originatingKSFiles = listOfNotNull(function.containingFile)
                )

            if (generateRegistry) registryRecords += RegistryGenerator.RegistryRecord(
                fullFunName = fullFunName,
                kiraProviderClassName = kiraProviderName,
                originatingKSFile = function.containingFile,
                providerWithEmptyConstructor = paramSuppliers.none { it is ParameterSupplier.Empty }
            )
        }

        if (generateRegistry) {
            val registryFile = RegistryGenerator.generate(registryRecords)
            val originatingKSFiles = buildList(
                capacity = registryRecords.count { it.originatingKSFile != null } + 1
            ) {
                kiraRootClass.containingFile?.let(::add)
                registryRecords.forEach { add(it.originatingKSFile ?: return@forEach) }
            }
            registryFile.writeTo(codeGenerator, aggregating = true, originatingKSFiles)
        }

        return emptyList()
    }

    private fun findSuppliersForFunParams(function: KSFunctionDeclaration) =
        function.parameters.map { param ->
            param.name ?: Errors.funParamWithNoName(function.qualifiedName!!.asString())
            val functionParameter = FunctionParameter(param)
            val supplierData = supplierProcessors.firstNotNullOfOrNull {
                it.provideSupplierFor(functionParameter)
            }

            if (supplierData != null)
                ParameterSupplier.Provided(functionParameter, supplierData)
            else
                ParameterSupplier.Empty(functionParameter)
        }

    private fun kiraProvider(
        injectorGenerator: InjectorGenerator,
        scopeName: ClassName,
        parameterSuppliers: List<ParameterSupplier>,
        kiraProviderName: ClassName,
        funName: MemberName
    ): TypeSpec {
        val kiraProvider = TypeSpec.classBuilder(kiraProviderName)
        val primaryConstructor = FunSpec.constructorBuilder()

        val needsMisses = parameterSuppliers.any { it is ParameterSupplier.Empty }
        if (needsMisses) {
            kiraProvider.addType(MissesClassGenerator.generate(parameterSuppliers))
            val missesClassType = kiraProviderName.nestedClass("Misses")
            val missesProviderType = LambdaTypeName
                .get(receiver = kiraScopeName, returnType = missesClassType)
            val missesProviderName = "missesProvider"
            primaryConstructor.addParameter(missesProviderName, missesProviderType)

            kiraProvider
                .addProperty(
                    PropertySpec.builder(missesProviderName, missesProviderType, KModifier.PRIVATE)
                        .initializer(missesProviderName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("misses", missesClassType, KModifier.PRIVATE)
                        .initializer("%T().%N()", kiraScopeName, missesProviderName)
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
            kiraProvider
                .addKdoc("@param injector wasn't generated because:\n")
                .addKdoc(injectorGenerator.noInjectorReasonMsg)
                .addProperty(
                    PropertySpec
                        .builder(injectorPropertyName, injectorProviderType, KModifier.PRIVATE)
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
                        scopeName, parameterSuppliers, kiraProviderName, injectorGenerator,
                        injectorPropertyName, funName
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
        funName: MemberName
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
                injectorGenerator
                    .injectorFunctionCall(kiraProviderName, parameterSuppliers, funName)
            )
            endControlFlow()
        }
        endControlFlow()
    }
}

private data class KiraFunction(
    val function: KSFunctionDeclaration,
    val kiraAnn: Kira,
)

private fun List<KiraFunction>.requireUniqueFunctionNames(): List<KiraFunction> {
    val uniquelyNamedFunctions = distinctBy(::correctedQualifiedName)
    if (uniquelyNamedFunctions.size != count())
        Errors.resolutionAmbiguity(duplicateFunctionsErrorMsg())
    return this
}

@OptIn(KspExperimental::class)
private fun KSFunctionDeclaration.kiraAnn(): Kira = getAnnotationsByType(Kira::class).single()

private fun correctedQualifiedName(kiraFunction: KiraFunction): String {
    val (function, kiraAnn) = kiraFunction
    val qualifiedName = function.qualifiedName!!.asString()
    val correctedSimpleName = kiraAnn.name.ifBlank { function.simpleName.asString() }
    return qualifiedName.replaceAfterLast('.', correctedSimpleName)
}

private fun Iterable<KiraFunction>.duplicateFunctionsErrorMsg(): String = this
    .map { it.function to correctedQualifiedName(it) }
    .findAllDuplicatesBy { (_, correctedQualifiedName) -> correctedQualifiedName }
    .distinctBy { it.second }
    .joinToString(prefix = "[\n", postfix = "\n]", separator = ",\n") { (function, _) ->
        buildString {
            append("\t" + function.kiraAnn().toString().replace('[', '(').replace(']', ')'))
            append(" " + function.qualifiedName!!.asString())
        }
    }

private fun <T, V> Iterable<T>.findAllDuplicatesBy(predicate: (T) -> V): Iterable<T> {
    val seen = mutableSetOf<V>()
    return filter { !seen.add(predicate(it)) }.toSet()
}
