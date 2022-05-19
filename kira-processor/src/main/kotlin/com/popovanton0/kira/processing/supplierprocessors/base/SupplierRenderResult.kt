package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.Misses

data class SupplierRenderResult constructor(
    val varName: String,
    val sourceCode: String,
    val supplierType: String?,
    val supplierImplType: String?,
    val scopeClassSource: String? = null,
    val misses: Misses.Class? = null,
    val imports: List<String>? = null
) {
    init {
        if (supplierImplType != null) requireNotNull(supplierType)
    }
}

