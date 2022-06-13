package com.popovanton0.kira.processing

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.popovanton0.kira.processing.supplierprocessors.BooleanSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.DataClassSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.EnumSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.ObjectSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.StringSupplierProcessor
import com.popovanton0.kira.processing.supplierprocessors.WholeNumberSupplierProcessor

class KiraProcessorProvider : SymbolProcessorProvider {
    public val supplierProcessors = listOf(
        WholeNumberSupplierProcessor,
        BooleanSupplierProcessor,
        StringSupplierProcessor,
        EnumSupplierProcessor,
        ObjectSupplierProcessor,
        DataClassSupplierProcessor,
    )

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KiraProcessor(
            environment = environment,
            supplierProcessors = supplierProcessors
        )
    }
}