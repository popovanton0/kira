package com.popovanton0.kira.processor

import org.junit.Test

class KiraProcessorTest : BaseProcessorTest() {
    @Test
    fun `no extension annotation of functional type`() {
        compileInputsAndVerifyOutputs()
    }
}