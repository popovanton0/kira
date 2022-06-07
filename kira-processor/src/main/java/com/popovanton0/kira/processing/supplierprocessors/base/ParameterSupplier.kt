package com.popovanton0.kira.processing.supplierprocessors.base

import com.popovanton0.kira.processing.FunctionParameter

internal sealed class ParameterSupplier(open val parameter: FunctionParameter) {
    internal data class Provided(
        override val parameter: FunctionParameter,
        val supplierData: SupplierData,
    ) : ParameterSupplier(parameter)

    internal data class Empty(
        override val parameter: FunctionParameter
    ) : ParameterSupplier(parameter)
}

