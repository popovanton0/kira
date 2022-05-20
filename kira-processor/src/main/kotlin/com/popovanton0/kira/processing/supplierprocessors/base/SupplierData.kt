package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.FunctionParameter
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName

data class SupplierData constructor(
    val functionParameter: FunctionParameter,
    val supplierInitializer: CodeBlock,
    val supplierImplType: TypeName,
)

