package com.popovanton0.kira.processing.generators

import com.google.devtools.ksp.symbol.KSFile
import com.popovanton0.kira.processing.KiraProcessor.Companion.KIRA_ROOT_PKG_NAME
import com.popovanton0.kira.processing.KiraProcessor.Companion.kiraProviderName
import com.popovanton0.kira.processing.supplierprocessors.base.SupplierProcessor
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock

internal object RegistryGenerator {
    private val kiraRegistryName =
        ClassName("$KIRA_ROOT_PKG_NAME.registry", "KiraRegistry")
    private val kiraProvidersMapType =
        MUTABLE_MAP.parameterizedBy(STRING, kiraProviderName.parameterizedBy(STAR))
    private val kiraProvidersHashMapType = ClassName("java.util", "HashMap")
        .parameterizedBy(STRING, kiraProviderName.parameterizedBy(STAR))
    private val kiraTodoFunName = MemberName(SupplierProcessor.SUPPLIERS_PKG_NAME, "TODO")

    internal data class RegistryRecord(
        val fullFunName: String,
        val kiraProviderClassName: ClassName,
        val providerWithEmptyConstructor: Boolean,
        val originatingKSFile: KSFile?,
    )

    fun generate(registryRecords: List<RegistryRecord>) = FileSpec.builder(
        packageName = kiraRegistryName.packageName,
        fileName = kiraRegistryName.simpleName
    )
        .addFileComment("This file is autogenerated. Do not edit it")
        .addType(
            TypeSpec.objectBuilder(kiraRegistryName)
                // val kiraProviders: MutableMap<String, KiraProvider<*>> =
                // HashMap(initialCapacity = (12 / 0.75f).toInt())
                .addProperty(
                    PropertySpec.builder("kiraProviders", kiraProvidersMapType)
                        .addKdoc(buildCodeBlock {
                            addStatement("key — fully qualified function name;")
                            addStatement("value — [%T] for that function", kiraProviderName)
                        })
                        .initializer(providersMap(registryRecords))
                        .build()
                )
                .build()
        )
        .build()

    private fun providersMap(registryRecords: List<RegistryRecord>) = buildCodeBlock {
        beginControlFlow(
            "%T((%L / 0.75f).toInt()).apply {",
            kiraProvidersHashMapType,
            registryRecords.size + 5
        )
        registryRecords.forEach { registryRecord ->
            if (registryRecord.providerWithEmptyConstructor) {
                addStatement(
                    "put(%S, %T())",
                    registryRecord.fullFunName,
                    registryRecord.kiraProviderClassName
                )
            } else {
                addStatement(
                    "put(%S, %M<%T>())",
                    registryRecord.fullFunName,
                    kiraTodoFunName,
                    registryRecord.kiraProviderClassName
                )
            }
        }
        endControlFlow()
    }
}