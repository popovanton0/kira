package com.popovanton0.kira.processing.supplierprocessors.base

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

interface ProcessingScope {
    fun processFunction(function: KSFunctionDeclaration): List<SupplierRenderResult>
}