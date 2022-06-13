package com.popovanton0.kira.processortest

import com.popovanton0.kira.processortest.base.BaseProcessorTest
import org.junit.Test

class KiraProcessorTest : BaseProcessorTest(UPDATE_TEST_OUTPUTS = false) {

    @Test
    fun another_test() =
        compileInputsAndVerifyOutputs()

    @Test
    fun data_class_processor() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_in_class() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_overloads_with_identical_specified_kira_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_overloads_with_specified_kira_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_overloads() =
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
    fun functions_with_strange_param_names() =
        compileInputsAndVerifyOutputs()

    @Test
    fun functions_with_supplierImplsScope_param_name() =
        compileInputsAndVerifyOutputs()

    @Test
    fun many_kira_roots() =
        compileInputsAndVerifyOutputs()

    @Test
    fun non_root_module() =
        compileInputsAndVerifyOutputs()

    @Test
    fun private_function() =
        compileInputsAndVerifyOutputs()

    @Test
    fun registry_generation() =
        compileInputsAndVerifyOutputs()

    @Test
    fun suspend_function() =
        compileInputsAndVerifyOutputs()

    @Test
    fun unknown_parameter_types() =
        compileInputsAndVerifyOutputs()
}