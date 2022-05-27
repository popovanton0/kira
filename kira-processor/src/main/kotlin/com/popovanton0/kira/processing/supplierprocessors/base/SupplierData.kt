package com.popovanton0.kira.processing.supplierprocessors.base

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName

data class SupplierData constructor(
    val implType: TypeName,
    val initializer: CodeBlock,
)

