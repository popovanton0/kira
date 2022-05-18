package com.popovanton0.kira.processor

import com.popovanton0.kira.processor.base.BaseProcessorTest
import org.junit.Test

class KiraProcessorTest : BaseProcessorTest() {

    @Test
    fun function_with_generics() =
        compileInputsAndVerifyOutputs()

    @Test
    fun another_test() =
        compileInputsAndVerifyOutputs()

    @Test
    fun circular_dependency_one_level() =
        compileInputsAndVerifyOutputs()

    @Test
    fun circular_dependency_many_levels() =
        compileInputsAndVerifyOutputs()
}