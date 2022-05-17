package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.FunctionParameter

interface ProcessingScope {
    fun processFunction(
        params: List<FunctionParameter>,
        missesPrefix: String
    ): List<SupplierRenderResult?>
}