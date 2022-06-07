package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.FunctionParameter
import com.popovanton0.kira.processing.KiraProcessor.Companion.KIRA_ROOT_PKG_NAME

interface SupplierProcessor {
    fun provideSupplierFor(param: FunctionParameter): SupplierData?

    companion object {
        const val SUPPLIERS_PKG_NAME = "$KIRA_ROOT_PKG_NAME.suppliers"
    }
}