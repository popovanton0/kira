package com.popovanton0.kira.processortest

import com.popovanton0.kira.processortest.base.BaseProcessorTest
import org.junit.Test

class KiraProcessorTest : BaseProcessorTest(UPDATE_TEST_OUTPUTS = true) {

    @Test
    fun function_with_no_params() =
        compileInputsAndVerifyOutputs()

    @Test
    fun function_with_generics() =
        compileInputsAndVerifyOutputs()

    @Test
    fun another_test() =
        compileInputsAndVerifyOutputs()

    @Test
    fun unknown_parameter_types() =
        compileInputsAndVerifyOutputs()
}