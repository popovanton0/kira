package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.FunctionParameter

interface SupplierProcessor {
    fun renderSupplier(
        processingScope: ProcessingScope,
        kiraAnn: Kira,
        param: FunctionParameter,
        missesPrefix: String,
        scopeClassPrefix: String,
    ): SupplierRenderResult?

    companion object {
        const val SUPPLIERS_PKG_NAME = "com.popovanton0.kira.suppliers"
        const val FULL_SUPPLIER_INTERFACE_NAME = "$SUPPLIERS_PKG_NAME.base.Supplier"
    }
}