package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.FunctionParameter

interface ProcessingScope {
    public val pkgName: String

    fun processFunction(
        params: List<FunctionParameter>,
        missesPrefix: String,
        scopeClassPrefix: String,
    ): List<SupplierData?>
}