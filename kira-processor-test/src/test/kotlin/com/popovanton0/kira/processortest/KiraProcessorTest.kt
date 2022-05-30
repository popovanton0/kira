package com.popovanton0.kira.processortest

import com.popovanton0.kira.processortest.base.BaseProcessorTest
import org.junit.Test

class KiraProcessorTest : BaseProcessorTest(UPDATE_TEST_OUTPUTS = true) {

    @Test
    fun another_test() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_in_class() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_extension_receiver() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_generics() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_no_params() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_unicode_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_unicode_name_and_specified_alternative_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_vararg_param() =
        compileInputsAndVerifyOutputs()

    @Test
    fun functions_with_scope_param_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun functions_with_strange_names() =
        compileInputsAndVerifyOutputs()

    @Test
    fun functions_with_supplierImplsScope_param_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun private_function() =
        compileInputsAndVerifyOutputs()

    @Test
    fun suspend_function() =
        compileInputsAndVerifyOutputs()

    @Test
    fun unknown_parameter_types() =
        compileInputsAndVerifyOutputs()
}