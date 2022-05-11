package com.popovanton0.kira.processor

import org.junit.Test

class KiraProcessorTest : BaseProcessorTest() {

    @Test
    fun `function with generics`() {
        assertCompilationFails("Functions with generics are not supported")
    }

    @Test
    fun `another test`() {
        compileInputsAndVerifyOutputs()
    }


}