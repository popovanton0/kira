package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.annotations.Kira
import com.popovanton0.kira.processing.Parameter

interface SupplierProcessor {
    fun ProcessingScope.renderSupplier(kiraAnn: Kira, parameter: Parameter): SupplierRenderResult?

    companion object {
        const val SUPPLIERS_PKG_NAME = "com.popovanton0.kira.suppliers"
        const val FULL_SUPPLIER_INTERFACE_NAME = "$SUPPLIERS_PKG_NAME.base.Supplier"
    }
}